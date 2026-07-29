package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RegexRepository
import com.example.data.SavedPatternEntity
import com.example.model.AppSettings
import com.example.model.AppThemeData
import com.example.model.CaptureGroup
import com.example.model.MatchResultItem
import com.example.model.PrebuiltPattern
import com.example.model.RegexFlag
import com.example.model.TutorialData
import com.example.model.TutorialLesson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.regex.PatternSyntaxException

data class RegexEvaluationState(
    val pattern: String = "",
    val testString: String = "",
    val replaceString: String = "",
    val flags: Set<RegexFlag> = setOf(RegexFlag.GLOBAL, RegexFlag.CASE_INSENSITIVE),
    val isReplaceMode: Boolean = false,
    val matches: List<MatchResultItem> = emptyList(),
    val replaceOutput: String = "",
    val error: String? = null,
    val isEvaluating: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RegexRepository

    private val _regexState = MutableStateFlow(RegexEvaluationState())
    val regexState: StateFlow<RegexEvaluationState> = _regexState.asStateFlow()

    // Selected navigation tab (0 = Tester, 1 = Cheat Sheet, 2 = Tutorials, 3 = Saved / History)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Active tutorial lesson
    private val _activeTutorialIndex = MutableStateFlow(0)
    val activeTutorialIndex: StateFlow<Int> = _activeTutorialIndex.asStateFlow()

    private val _tutorialInputPattern = MutableStateFlow("")
    val tutorialInputPattern: StateFlow<String> = _tutorialInputPattern.asStateFlow()

    private val _showHint = MutableStateFlow(false)
    val showHint: StateFlow<Boolean> = _showHint.asStateFlow()

    private val prefs = application.getSharedPreferences("regex_lab_prefs", Context.MODE_PRIVATE)

    private val _appSettings = MutableStateFlow(loadSettingsFromPrefs())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private fun loadSettingsFromPrefs(): AppSettings {
        return AppSettings(
            themeId = prefs.getString("theme_id", "teal") ?: "teal",
            hideCheatSheet = prefs.getBoolean("hide_cheat_sheet", false),
            hideTutorials = prefs.getBoolean("hide_tutorials", false),
            hideSaved = prefs.getBoolean("hide_saved", false),
            hideAllBottomBar = prefs.getBoolean("hide_all_bottom_bar", false),
            hideSaveButton = prefs.getBoolean("hide_save_button", false)
        )
    }

    fun updateTheme(themeId: String) {
        prefs.edit().putString("theme_id", themeId).apply()
        _appSettings.value = _appSettings.value.copy(themeId = themeId)
    }

    fun updateHideCheatSheet(hide: Boolean) {
        prefs.edit().putBoolean("hide_cheat_sheet", hide).apply()
        _appSettings.value = _appSettings.value.copy(hideCheatSheet = hide)
    }

    fun updateHideTutorials(hide: Boolean) {
        prefs.edit().putBoolean("hide_tutorials", hide).apply()
        _appSettings.value = _appSettings.value.copy(hideTutorials = hide)
    }

    fun updateHideSaved(hide: Boolean) {
        prefs.edit().putBoolean("hide_saved", hide).apply()
        _appSettings.value = _appSettings.value.copy(hideSaved = hide)
    }

    fun updateHideAllBottomBar(hide: Boolean) {
        prefs.edit().putBoolean("hide_all_bottom_bar", hide).apply()
        _appSettings.value = _appSettings.value.copy(hideAllBottomBar = hide)
    }

    fun updateHideSaveButton(hide: Boolean) {
        prefs.edit().putBoolean("hide_save_button", hide).apply()
        _appSettings.value = _appSettings.value.copy(hideSaveButton = hide)
    }

    init {
        val dao = AppDatabase.getDatabase(application).regexDao()
        repository = RegexRepository(dao)

        val savedFlagsStr = prefs.getString("saved_flags", "gi") ?: "gi"
        val initialFlags = parseFlags(savedFlagsStr)
        _regexState.value = _regexState.value.copy(flags = initialFlags)

        // Clean up any historical duplicate entries in database
        viewModelScope.launch {
            repository.deleteDuplicateHistory()
        }

        // Initial regex evaluation
        evaluateRegex()
    }

    private fun parseFlags(flagsStr: String): Set<RegexFlag> {
        val set = mutableSetOf<RegexFlag>()
        if (flagsStr.contains('g')) set.add(RegexFlag.GLOBAL)
        if (flagsStr.contains('i')) set.add(RegexFlag.CASE_INSENSITIVE)
        if (flagsStr.contains('m')) set.add(RegexFlag.MULTILINE)
        if (flagsStr.contains('s')) set.add(RegexFlag.SINGLELINE)
        return set
    }

    private fun saveFlags(flags: Set<RegexFlag>) {
        val flagsStr = flags.map { it.code }.joinToString("")
        prefs.edit().putString("saved_flags", flagsStr).apply()
    }

    val savedPatterns: StateFlow<List<SavedPatternEntity>> = repository.savedPatterns
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tutorialProgress = repository.tutorialProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun updatePattern(newPattern: String) {
        _regexState.value = _regexState.value.copy(pattern = newPattern)
        evaluateRegex()
    }

    fun updateTestString(newText: String) {
        _regexState.value = _regexState.value.copy(testString = newText)
        evaluateRegex()
    }

    fun updateReplaceString(newReplace: String) {
        _regexState.value = _regexState.value.copy(replaceString = newReplace)
        evaluateRegex()
    }

    fun commitCurrentSessionToHistory() {
        viewModelScope.launch {
            val currentState = _regexState.value
            if (currentState.pattern.isNotBlank() || currentState.testString.isNotBlank()) {
                val flagCodes = currentState.flags.map { it.code }.joinToString("")
                val title = if (currentState.pattern.isNotBlank()) {
                    "/${currentState.pattern}/"
                } else {
                    "Snippet: ${currentState.testString.replace("\n", " ").take(25)}"
                }

                val currentHistory = savedPatterns.value.filter { it.category == "History" }
                val exactMatch = currentHistory.firstOrNull {
                    it.pattern == currentState.pattern &&
                    it.flags == flagCodes &&
                    it.testString == currentState.testString &&
                    it.replaceString == currentState.replaceString
                }

                if (exactMatch != null) {
                    repository.savePattern(
                        exactMatch.copy(
                            title = title,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                } else {
                    repository.savePattern(
                        SavedPatternEntity(
                            title = title,
                            pattern = currentState.pattern,
                            flags = flagCodes,
                            testString = currentState.testString,
                            replaceString = currentState.replaceString,
                            category = "History",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                repository.deleteDuplicateHistory()
            }
        }
    }

    fun clearTestString() {
        commitCurrentSessionToHistory()
        updateTestString("")
    }

    fun clearPattern() {
        commitCurrentSessionToHistory()
        updatePattern("")
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
        }
    }

    fun toggleFlag(flag: RegexFlag) {
        val currentFlags = _regexState.value.flags.toMutableSet()
        if (currentFlags.contains(flag)) {
            currentFlags.remove(flag)
        } else {
            currentFlags.add(flag)
        }
        _regexState.value = _regexState.value.copy(flags = currentFlags)
        saveFlags(currentFlags)
        evaluateRegex()
    }

    fun toggleReplaceMode(enabled: Boolean) {
        _regexState.value = _regexState.value.copy(isReplaceMode = enabled)
        evaluateRegex()
    }

    fun loadPrebuiltPattern(prebuilt: PrebuiltPattern) {
        commitCurrentSessionToHistory()
        val flagsSet = mutableSetOf<RegexFlag>()
        if (prebuilt.flags.contains('g')) flagsSet.add(RegexFlag.GLOBAL)
        if (prebuilt.flags.contains('i')) flagsSet.add(RegexFlag.CASE_INSENSITIVE)
        if (prebuilt.flags.contains('m')) flagsSet.add(RegexFlag.MULTILINE)
        if (prebuilt.flags.contains('s')) flagsSet.add(RegexFlag.SINGLELINE)

        _regexState.value = _regexState.value.copy(
            pattern = prebuilt.pattern,
            testString = prebuilt.sampleText,
            replaceString = prebuilt.replacePattern,
            flags = flagsSet
        )
        evaluateRegex()
        _selectedTab.value = 0 // Switch to tester screen
    }

    fun loadCustomPattern(title: String, pattern: String, flagsStr: String, testStr: String, replaceStr: String) {
        commitCurrentSessionToHistory()
        val flagsSet = mutableSetOf<RegexFlag>()
        if (flagsStr.contains('g')) flagsSet.add(RegexFlag.GLOBAL)
        if (flagsStr.contains('i')) flagsSet.add(RegexFlag.CASE_INSENSITIVE)
        if (flagsStr.contains('m')) flagsSet.add(RegexFlag.MULTILINE)
        if (flagsStr.contains('s')) flagsSet.add(RegexFlag.SINGLELINE)

        _regexState.value = _regexState.value.copy(
            pattern = pattern,
            testString = testStr,
            replaceString = replaceStr,
            flags = flagsSet
        )
        evaluateRegex()
        _selectedTab.value = 0
    }

    fun saveCurrentPattern(title: String, category: String) {
        viewModelScope.launch {
            val currentState = _regexState.value
            val flagCodes = currentState.flags.map { it.code }.joinToString("")
            repository.savePattern(
                SavedPatternEntity(
                    title = title.ifEmpty { "Untitled Pattern" },
                    pattern = currentState.pattern,
                    flags = flagCodes,
                    testString = currentState.testString,
                    replaceString = currentState.replaceString,
                    category = category
                )
            )
        }
    }

    fun deleteSavedPattern(id: Long) {
        viewModelScope.launch {
            repository.deletePattern(id)
        }
    }

    private fun evaluateRegex() {
        val patternStr = _regexState.value.pattern
        val testStr = _regexState.value.testString
        val replaceStr = _regexState.value.replaceString
        val flags = _regexState.value.flags

        if (patternStr.isEmpty()) {
            _regexState.value = _regexState.value.copy(
                matches = emptyList(),
                replaceOutput = testStr,
                error = null
            )
            return
        }

        try {
            val kotlinOptions = flags.mapNotNull { it.option }.toSet()
            val regex = Regex(patternStr, kotlinOptions)
            val isGlobal = flags.contains(RegexFlag.GLOBAL)

            val matchSequence = if (isGlobal) {
                regex.findAll(testStr)
            } else {
                val singleMatch = regex.find(testStr)
                if (singleMatch != null) sequenceOf(singleMatch) else emptySequence()
            }

            val matchResults = matchSequence.mapIndexed { idx, match ->
                val captureGroups = match.groups.mapIndexedNotNull { gIdx, group ->
                    if (group != null) {
                        CaptureGroup(
                            index = gIdx,
                            name = null,
                            value = group.value,
                            range = group.range
                        )
                    } else null
                }
                MatchResultItem(
                    matchIndex = idx,
                    value = match.value,
                    range = match.range,
                    groups = captureGroups
                )
            }.toList()

            val replaceRes = try {
                if (_regexState.value.isReplaceMode) {
                    if (isGlobal) {
                        regex.replace(testStr, replaceStr)
                    } else {
                        regex.replaceFirst(testStr, replaceStr)
                    }
                } else ""
            } catch (e: Exception) {
                "Replace Error: ${e.localizedMessage}"
            }

            _regexState.value = _regexState.value.copy(
                matches = matchResults,
                replaceOutput = replaceRes,
                error = null
            )
        } catch (e: PatternSyntaxException) {
            _regexState.value = _regexState.value.copy(
                matches = emptyList(),
                replaceOutput = "",
                error = "Syntax Error: ${e.description ?: e.message}"
            )
        } catch (e: Exception) {
            _regexState.value = _regexState.value.copy(
                matches = emptyList(),
                replaceOutput = "",
                error = e.localizedMessage ?: "Invalid Regex Expression"
            )
        }
    }

    // Tutorial methods
    fun selectTutorialLesson(index: Int) {
        if (index in TutorialData.lessons.indices) {
            _activeTutorialIndex.value = index
            val lesson = TutorialData.lessons[index]
            _tutorialInputPattern.value = lesson.initialPattern
            _showHint.value = false
        }
    }

    fun updateTutorialPattern(pattern: String) {
        _tutorialInputPattern.value = pattern
    }

    fun toggleTutorialHint() {
        _showHint.value = !_showHint.value
    }

    fun submitTutorialSolution() {
        val currentLesson = TutorialData.lessons[_activeTutorialIndex.value]
        val userPattern = _tutorialInputPattern.value
        val isPassed = currentLesson.validate(userPattern, currentLesson.sampleText)

        if (isPassed) {
            viewModelScope.launch {
                repository.markLessonCompleted(currentLesson.id, userPattern)
            }
        }
    }
}
