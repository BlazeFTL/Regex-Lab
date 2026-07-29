package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
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
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Teal100
import com.example.ui.theme.Teal50
import com.example.ui.theme.Teal600
import com.example.viewmodel.MainViewModel

/**
 * VisualTransformation that dynamically highlights regex matches inside the editable text input box.
 */
class RegexHighlightTransformation(
    private val matches: List<MatchResultItem>,
    private val selectedMatchIndex: Int?
) : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): TransformedText {
        if (text.isEmpty() || matches.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val annotatedString = buildAnnotatedString {
            append(text.text)

            matches.forEach { matchItem ->
                val start = matchItem.range.first.coerceIn(0, text.length)
                val end = (matchItem.range.last + 1).coerceIn(0, text.length)

                if (start < end) {
                    val colorIndex = matchItem.matchIndex % MatchHighlights.size
                    val colorPair = MatchHighlights[colorIndex]
                    val isSelected = selectedMatchIndex == matchItem.matchIndex

                    addStyle(
                        style = SpanStyle(
                            background = if (isSelected) colorPair.border else colorPair.bg,
                            color = colorPair.text,
                            fontWeight = FontWeight.Bold
                        ),
                        start = start,
                        end = end
                    )
                }
            }
        }
        return TransformedText(annotatedString, OffsetMapping.Identity)
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

    var showSaveDialog by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showFlagsMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. TOP HEADER WITH TITLE AND HISTORY/SAVE ACTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Regex Lab Tester",
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

            Row {
                // HISTORY BUTTON (Replaced pre-built library)
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

        // 2. TEXT INPUT BOX - TAKES MAJORITY OF THE SCREEN
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
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

                        // CLEAR BUTTON - RIGHT SIDE OF CHARS COUNT
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

                Spacer(modifier = Modifier.height(10.dp))

                val transformation = remember(state.matches) {
                    RegexHighlightTransformation(state.matches, null)
                }

                // Main Editable Text Field occupying full vertical space
                OutlinedTextField(
                    value = state.testString,
                    onValueChange = { viewModel.updateTestString(it) },
                    modifier = Modifier.fillMaxSize(),
                    visualTransformation = transformation,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    ),
                    placeholder = { Text("Paste Or Type Here", fontFamily = FontFamily.Monospace) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal600,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Slate50,
                        unfocusedContainerColor = Slate50
                    )
                )
            }
        }

        // 3. REGEX PATTERN BOX & CONTROLS AT BOTTOM
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
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
                        // REPLACE MODE TOGGLE BUTTON NEAR X/ACTIONS
                        IconButton(
                            onClick = { viewModel.toggleReplaceMode(!state.isReplaceMode) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FindReplace,
                                contentDescription = "Toggle Replace",
                                tint = if (state.isReplaceMode) Indigo600 else Slate600,
                                modifier = Modifier.size(20.dp)
                            )
                        }

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

                Spacer(modifier = Modifier.height(8.dp))

                // REGEX PATTERN INPUT FIELD (Supports full multiline wrap, no cutoffs)
                OutlinedTextField(
                    value = state.pattern,
                    onValueChange = { viewModel.updatePattern(it) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    ),
                    placeholder = {
                        Text(
                            "Enter expression...",
                            fontFamily = FontFamily.Monospace,
                            color = Slate300
                        )
                    },
                    leadingIcon = {
                        Text(
                            "/",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Teal600,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            val activeFlagsStr = state.flags.map { it.code }.joinToString("")
                            Text(
                                "/" + activeFlagsStr,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Indigo600
                            )

                            // FLAG ICON BUTTON - OPENS DROPDOWN POPUP
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
                                    modifier = Modifier
                                        .background(Color.White)
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = "  Regex Flags",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate600,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    RegexFlag.entries.forEach { flag ->
                                        val isSelected = state.flags.contains(flag)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.toggleFlag(flag) }
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isSelected,
                                                onCheckedChange = { viewModel.toggleFlag(flag) },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = Teal600,
                                                    uncheckedColor = Slate300
                                                ),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "${flag.flagName} (${flag.code})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Slate900
                                                )
                                                Text(
                                                    text = flag.description,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Slate600,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal600,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Slate50,
                        unfocusedContainerColor = Slate50
                    ),
                    singleLine = false,
                    maxLines = 4
                )

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

                // REPLACE / SUBSTITUTION INPUT FIELD (Visible when Replace Mode is active)
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
                            colors = OutlinedTextFieldDefaults.colors(
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

    // HISTORY BOTTOM SHEET
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
                            text = "Evaluation History & Saved",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                }

                Text(
                    text = "Tap any entry to restore pattern and test input into tester",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (savedPatterns.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No saved patterns in history yet.\nClick the Save button in top right to store your current test!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(340.dp)
                    ) {
                        items(savedPatterns) { entity ->
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
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "/${entity.pattern}/${entity.flags}",
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
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
                Text("Save to History Library", fontWeight = FontWeight.Bold)
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
