package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.AnnotatedString
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
import com.example.model.PrebuiltPatternLibrary
import com.example.model.RegexFlag
import com.example.ui.theme.Emerald500
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
    private val selectedMatchIndex: Int? = null
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty() || matches.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val annotatedString = buildAnnotatedString {
            append(raw)
            val sortedMatches = matches.sortedBy { it.range.first }
            sortedMatches.forEachIndexed { idx, match ->
                val start = match.range.first.coerceIn(0, raw.length)
                val end = (match.range.last + 1).coerceIn(0, raw.length)
                if (start < end) {
                    val colorPair = MatchHighlights[idx % MatchHighlights.size]
                    val isSelected = selectedMatchIndex == match.matchIndex
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegexTesterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.regexState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }
    var showPresetSheet by remember { mutableStateOf(false) }
    var selectedMatchItem by remember { mutableStateOf<MatchResultItem?>(null) }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }

            // 1. TITLE & ACTION HEADER
            item {
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
                        IconButton(
                            onClick = { showPresetSheet = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Teal50)
                                .border(1.dp, Teal100, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DataObject,
                                contentDescription = "Prebuilt Recipes",
                                tint = Teal600
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

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
            }

            // 2. TEXT INPUT BOX AT TOP (MATCHES HIGHLIGHTED DIRECTLY IN THIS BOX)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(${state.testString.length} chars)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val transformation = remember(state.matches, selectedMatchItem) {
                            RegexHighlightTransformation(state.matches, selectedMatchItem?.matchIndex)
                        }

                        OutlinedTextField(
                            value = state.testString,
                            onValueChange = { viewModel.updateTestString(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            visualTransformation = transformation,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            placeholder = { Text("Paste or type text to test regex against...") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal600,
                                unfocusedBorderColor = Slate200,
                                focusedContainerColor = Slate50,
                                unfocusedContainerColor = Slate50
                            )
                        )

                        // Quick Match Chips below text input box
                        if (state.matches.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Quick Match Chips:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                state.matches.take(12).forEachIndexed { idx, match ->
                                    val colorPair = MatchHighlights[idx % MatchHighlights.size]
                                    val isSelected = selectedMatchItem?.matchIndex == match.matchIndex

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isSelected) colorPair.border else colorPair.bg)
                                            .border(1.dp, colorPair.border, RoundedCornerShape(16.dp))
                                            .clickable { selectedMatchItem = if (isSelected) null else match }
                                            .padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "#${match.matchIndex + 1}: ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorPair.text,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (match.value.length > 18) match.value.take(15) + "..." else match.value,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontFamily = FontFamily.Monospace,
                                            color = colorPair.text,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                if (state.matches.size > 12) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Slate200)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "+${state.matches.size - 12} more",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate800,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. REGEX PATTERN INPUT CARD (BELOW TEXT INPUT BOX)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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

                            if (state.pattern.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updatePattern("") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = Slate600
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.pattern,
                            onValueChange = { viewModel.updatePattern(it) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            placeholder = {
                                Text(
                                    "Enter regex e.g. [a-z]+",
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
                                Text(
                                    "/" + state.flags.map { it.code }.joinToString(""),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Indigo600,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal600,
                                unfocusedBorderColor = Slate200,
                                focusedContainerColor = Slate50,
                                unfocusedContainerColor = Slate50
                            ),
                            singleLine = true
                        )

                        // Error Banner
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // REGEX FLAG TOGGLES (u AND x FLAGS REMOVED)
                        Text(
                            text = "Regex Modifier Flags:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            RegexFlag.entries.forEach { flag ->
                                val isSelected = state.flags.contains(flag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.toggleFlag(flag) },
                                    label = {
                                        Text(
                                            text = "${flag.code} (${flag.flagName})",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    leadingIcon = if (isSelected) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Teal100,
                                        selectedLabelColor = Teal600,
                                        selectedLeadingIconColor = Teal600,
                                        containerColor = Slate100,
                                        labelColor = Slate600
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. REPLACEMENT & SUBSTITUTION PREVIEW MODE
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FindReplace,
                                    contentDescription = "Replace Mode",
                                    tint = Indigo600,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "SUBSTITUTION / REPLACE PREVIEW",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600,
                                    letterSpacing = 1.sp
                                )
                            }

                            Switch(
                                checked = state.isReplaceMode,
                                onCheckedChange = { viewModel.toggleReplaceMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Indigo600
                                )
                            )
                        }

                        AnimatedVisibility(visible = state.isReplaceMode) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = "Replacement String ($0 = full match, $1 = group 1):",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = state.replaceString,
                                    onValueChange = { viewModel.updateReplaceString(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    placeholder = { Text("e.g. [REPLACED: $0]") },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Indigo600,
                                        unfocusedBorderColor = Slate200,
                                        focusedContainerColor = Slate50,
                                        unfocusedContainerColor = Slate50
                                    ),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Transformed Output Result:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                SelectionContainer {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Indigo50)
                                            .border(1.dp, Indigo100, RoundedCornerShape(8.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = state.replaceOutput.ifEmpty { "Transformed result will appear here..." },
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            color = Slate900
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. CAPTURED GROUPS BREAKDOWN
            if (state.matches.isNotEmpty()) {
                item {
                    Text(
                        text = "Match Details & Capture Groups (${state.matches.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(state.matches) { match ->
                    val colorPair = MatchHighlights[match.matchIndex % MatchHighlights.size]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(colorPair.bg)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Match #${match.matchIndex + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorPair.text,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Index ${match.range.first}..${match.range.last}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(match.value))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Match",
                                        tint = Slate600,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Slate100)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = match.value,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            }

                            if (match.groups.size > 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Captured Groups (${match.groups.size - 1}):",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate600
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                match.groups.drop(1).forEach { group ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Group ${group.index}: ",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Indigo600
                                        )
                                        Text(
                                            text = "\"${group.value}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontFamily = FontFamily.Monospace,
                                            color = Slate800
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "(${group.range.first}..${group.range.last})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // PREBUILT PATTERNS SHEET
    if (showPresetSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPresetSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Pre-built Regex Pattern Library",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Tap any recipe to load into real-time tester",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(380.dp)
                ) {
                    items(PrebuiltPatternLibrary.items) { preset ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.loadPrebuiltPattern(preset)
                                    showPresetSheet = false
                                },
                            colors = CardDefaults.cardColors(containerColor = Slate50),
                            border = CardDefaults.outlinedCardBorder(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Teal600
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Teal100)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = preset.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Teal600,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = preset.pattern,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = Slate900,
                                    fontWeight = FontWeight.SemiBold
                                )
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
        var categoryInput by remember { mutableStateOf("Custom") }

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Text("Save Regex Pattern", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Save current pattern and test string to your local database library.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Pattern Title") },
                        placeholder = { Text("e.g. Email Validator") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("Category Tag") },
                        placeholder = { Text("e.g. Web / Validation / Code") },
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
                    Text("Save to Library")
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
