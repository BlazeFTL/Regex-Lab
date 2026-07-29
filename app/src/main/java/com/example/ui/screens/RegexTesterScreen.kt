package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchResultItem
import com.example.model.RegexFlag
import com.example.ui.theme.Indigo100
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
import com.example.ui.theme.MatchHighlights
import com.example.ui.theme.Rose500
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900
import com.example.ui.theme.Teal100
import com.example.ui.theme.Teal50
import com.example.ui.theme.Teal600
import com.example.viewmodel.MainViewModel

/**
 * VisualTransformation that dynamically highlights regex matches and forces character-level line breaks.
 */
class RegexHighlightTransformation(
    private val matches: List<MatchResultItem>,
    private val selectedMatchIndex: Int?
) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        if (text.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val builder = StringBuilder()
        for (i in 0 until text.length) {
            builder.append(text.text[i])
            builder.append('\u200B')
        }

        val annotatedString = buildAnnotatedString {
            append(builder.toString())

            if (matches.isNotEmpty()) {
                matches.forEach { matchItem ->
                    val origStart = matchItem.range.first.coerceIn(0, text.length)
                    val origEnd = (matchItem.range.last + 1).coerceIn(0, text.length)

                    if (origStart < origEnd) {
                        val transStart = (origStart * 2).coerceIn(0, builder.length)
                        val transEnd = (origEnd * 2).coerceIn(0, builder.length)

                        val colorIndex = matchItem.matchIndex % MatchHighlights.size
                        val colorPair = MatchHighlights[colorIndex]
                        val isSelected = selectedMatchIndex == matchItem.matchIndex

                        addStyle(
                            style = SpanStyle(
                                background = if (isSelected) colorPair.border else colorPair.bg,
                                color = colorPair.text,
                                fontWeight = FontWeight.Bold
                            ),
                            start = transStart,
                            end = transEnd
                        )
                    }
                }
            }
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return (offset * 2).coerceIn(0, annotatedString.length)
            }
            override fun transformedToOriginal(offset: Int): Int {
                return (offset / 2).coerceIn(0, text.length)
            }
        }

        return TransformedText(annotatedString, mapping)
    }
}

/**
 * VisualTransformation that forces character-level line breaking for expression inputs.
 */
object CharBreakTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        if (text.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val builder = StringBuilder()
        for (i in 0 until text.length) {
            builder.append(text.text[i])
            builder.append('\u200B')
        }

        val annotatedString = buildAnnotatedString {
            append(builder.toString())
        }

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return (offset * 2).coerceIn(0, annotatedString.length)
            }
            override fun transformedToOriginal(offset: Int): Int {
                return (offset / 2).coerceIn(0, text.length)
            }
        }

        return TransformedText(annotatedString, mapping)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegexTesterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.regexState.collectAsState()
    val savedPatterns by viewModel.savedPatterns.collectAsState()
    val historyList = remember(savedPatterns) {
        savedPatterns.filter { it.category == "History" }
            .distinctBy { Triple(it.pattern, it.testString, Pair(it.flags, it.replaceString)) }
    }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showFlagsMenu by remember { mutableStateOf(false) }

    val testTextScrollState = rememberScrollState()
    val testTextFocusRequester = remember { FocusRequester() }
    val regexPatternFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-scroll Test Text to keep caret/bottom in view as user types or adds newlines
    LaunchedEffect(state.testString) {
        if (state.testString.isNotEmpty()) {
            testTextScrollState.animateScrollTo(testTextScrollState.maxValue)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. TOP HEADER WITH TITLE AND HISTORY/SAVE ACTIONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Regex Lab",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "By BlazeFTL",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Teal600
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // HISTORY BUTTON
                    IconButton(
                        onClick = { showHistorySheet = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Teal50)
                            .border(1.dp, Teal100, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Evaluation History",
                            tint = Teal600
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // SAVE BUTTON
                    IconButton(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Indigo50)
                            .border(1.dp, Indigo100, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Pattern",
                            tint = Indigo600
                        )
                    }
                }
            }

            // 2. MAIN CONTAINER CARD WITH OVERLAY REGEX PATTERN AT BOTTOM
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // A. TEST TEXT INPUT CONTAINER (SCROLLABLE UNDER OVERLAY)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        // Header row inside Text Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TEST TEXT INPUT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate600,
                                letterSpacing = 1.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (state.matches.isNotEmpty()) Teal600 else Slate300)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${state.matches.size} match${if (state.matches.size != 1) "es" else ""}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.matches.isNotEmpty()) Teal600 else Slate600
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${state.testString.length} chars)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.width(4.dp))

                                // CLEAR BUTTON
                                if (state.testString.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.updateTestString("") },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Text",
                                            tint = Slate600,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        val transformation = remember(state.matches) {
                            RegexHighlightTransformation(state.matches, null)
                        }

                        // Main Editable Text Area taking full space, scrollable
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    testTextFocusRequester.requestFocus()
                                    keyboardController?.show()
                                }
                                .verticalScroll(testTextScrollState)
                        ) {
                            if (state.testString.isEmpty()) {
                                Text(
                                    text = "Paste Or Type Here...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate300,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                BasicTextField(
                                    value = state.testString,
                                    onValueChange = { viewModel.updateTestString(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(testTextFocusRequester),
                                    visualTransformation = transformation,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp,
                                        color = Slate900,
                                        lineBreak = LineBreak(
                                            strategy = LineBreak.Strategy.Simple,
                                            strictness = LineBreak.Strictness.Strict,
                                            wordBreak = LineBreak.WordBreak.Default
                                        )
                                    ),
                                    cursorBrush = SolidColor(Teal600)
                                )

                                // Generous bottom spacer so text scrolls cleanly above the floating Regex Box overlay
                                Spacer(modifier = Modifier.height(if (state.isReplaceMode) 220.dp else 120.dp))
                            }
                        }
                    }

                    // B. REGEX EXPRESSION PATTERN CARD (OVERLAY FLOATING AT BOTTOM)
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(6.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                regexPatternFocusRequester.requestFocus()
                                keyboardController?.show()
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Slate200)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "EXPRESSION PATTERN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600,
                                    letterSpacing = 1.sp
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // REPLACE MODE CHIP TOGGLE
                                    FilterChip(
                                        selected = state.isReplaceMode,
                                        onClick = { viewModel.toggleReplaceMode(!state.isReplaceMode) },
                                        label = { Text(if (state.isReplaceMode) "Replace ON" else "Replace Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.FindReplace,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Indigo600,
                                            selectedLabelColor = Color.White,
                                            selectedLeadingIconColor = Color.White,
                                            containerColor = Slate100,
                                            labelColor = Slate900
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // CLEAR PATTERN BUTTON
                                    if (state.pattern.isNotEmpty()) {
                                        IconButton(
                                            onClick = { viewModel.updatePattern("") },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear Pattern",
                                                tint = Slate600,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // REGEX PATTERN INPUT FIELD
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "/",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Teal600,
                                    modifier = Modifier.padding(end = 4.dp)
                                )

                                BasicTextField(
                                    value = state.pattern,
                                    onValueChange = { viewModel.updatePattern(it) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(regexPatternFocusRequester),
                                    visualTransformation = CharBreakTransformation,
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate900,
                                        lineHeight = 20.sp,
                                        lineBreak = LineBreak(
                                            strategy = LineBreak.Strategy.Simple,
                                            strictness = LineBreak.Strictness.Strict,
                                            wordBreak = LineBreak.WordBreak.Default
                                        )
                                    ),
                                    cursorBrush = SolidColor(Teal600),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                                                .background(Slate50, RoundedCornerShape(10.dp))
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            if (state.pattern.isEmpty()) {
                                                Text(
                                                    "Enter expression...",
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 14.sp,
                                                    color = Slate300
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    val activeFlagsStr = state.flags.map { it.code }.joinToString("")
                                    Text(
                                        "/" + activeFlagsStr,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo600
                                    )

                                    // FLAG ICON BUTTON
                                    Box {
                                        IconButton(
                                            onClick = { showFlagsMenu = true },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Flag,
                                                contentDescription = "Regex Flags",
                                                tint = Teal600,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // DROPDOWN POPUP FOR FLAGS
                                        DropdownMenu(
                                            expanded = showFlagsMenu,
                                            onDismissRequest = { showFlagsMenu = false },
                                            modifier = Modifier.background(Color.White)
                                        ) {
                                            Text(
                                                text = "Regex Flags",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate900,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                            RegexFlag.entries.forEach { flag ->
                                                val isSelected = state.flags.contains(flag)
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { viewModel.toggleFlag(flag) }
                                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Checkbox(
                                                        checked = isSelected,
                                                        onCheckedChange = { viewModel.toggleFlag(flag) },
                                                        colors = CheckboxDefaults.colors(checkedColor = Teal600)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Column {
                                                        Text(
                                                            text = "${flag.flagName} (${flag.code})",
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Slate900
                                                        )
                                                        Text(
                                                            text = flag.description,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = Slate600
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Error Message Display
                            AnimatedVisibility(visible = state.error != null) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = "Error",
                                            tint = Rose500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = state.error ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF991B1B),
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            // REPLACE / SUBSTITUTION INPUT FIELD
                            AnimatedVisibility(visible = state.isReplaceMode) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Text(
                                        text = "Replace Pattern ($0 = full match, $1 = group 1):",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))

                                    OutlinedTextField(
                                        value = state.replaceString,
                                        onValueChange = { viewModel.updateReplaceString(it) },
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        placeholder = { Text("e.g. [REPLACED: $0]") },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Indigo600,
                                            unfocusedBorderColor = Slate200,
                                            focusedContainerColor = Slate50,
                                            unfocusedContainerColor = Slate50
                                        ),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    SelectionContainer {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Indigo50)
                                                .border(1.dp, Indigo100, RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = state.replaceOutput.ifEmpty { "Transformed text output will appear here..." },
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 12.sp,
                                                color = Slate900
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // HISTORY BOTTOM SHEET (Auto-Saved History)
    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Teal600
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Evaluation History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }

                    if (historyList.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearAllHistory() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear History",
                                tint = Rose500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear History", color = Rose500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "Automatically logs tests as you type. Tap any entry to restore.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (historyList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No history recorded yet.\nStart typing expressions in the tester to auto-log your work!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(340.dp)
                    ) {
                        items(historyList) { entity ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.loadCustomPattern(
                                            title = entity.title,
                                            pattern = entity.pattern,
                                            flagsStr = entity.flags,
                                            testStr = entity.testString,
                                            replaceStr = entity.replaceString
                                        )
                                        showHistorySheet = false
                                    },
                                colors = CardDefaults.cardColors(containerColor = Slate50),
                                border = CardDefaults.outlinedCardBorder(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = entity.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Teal600
                                        )
                                        Text(
                                            text = entity.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600
                                        )
                                    }
                                    if (entity.pattern.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "/${entity.pattern}/${entity.flags}",
                                            fontFamily = FontFamily.Monospace,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                    }
                                    if (entity.testString.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Text: ${if (entity.testString.length > 50) entity.testString.take(50) + "..." else entity.testString}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // SAVE PATTERN DIALOG
    if (showSaveDialog) {
        var titleInput by remember { mutableStateOf("") }
        var categoryInput by remember { mutableStateOf("General") }

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text("Save to Library", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Save current text and regex expression so you can reload it anytime.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Title") },
                        placeholder = { Text("e.g. Email Extractor Test") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("Category") },
                        placeholder = { Text("e.g. Work / Testing / Snippets") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentPattern(titleInput, categoryInput)
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal600)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
