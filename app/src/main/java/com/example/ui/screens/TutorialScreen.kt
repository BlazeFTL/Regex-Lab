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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchResultItem
import com.example.model.TutorialData
import com.example.ui.components.HighlightedCodeText
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo100
import com.example.ui.theme.Indigo50
import com.example.ui.theme.Indigo600
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
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val activeIndex by viewModel.activeTutorialIndex.collectAsState()
    val userPattern by viewModel.tutorialInputPattern.collectAsState()
    val showHint by viewModel.showHint.collectAsState()
    val progressList by viewModel.tutorialProgress.collectAsState()

    val completedIds = remember(progressList) {
        progressList.filter { it.isCompleted }.map { it.lessonId }.toSet()
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = activeIndex,
        pageCount = { TutorialData.lessons.size }
    )

    // Sync ViewModel activeIndex with PagerState
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != activeIndex) {
            viewModel.selectTutorialLesson(pagerState.currentPage)
        }
    }

    // Sync ViewModel change to pager
    LaunchedEffect(activeIndex) {
        if (pagerState.currentPage != activeIndex) {
            pagerState.animateScrollToPage(activeIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
    ) {
        // TOP PROGRESS HEADER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Tutorial",
                            tint = Teal600,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Regex Academy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Teal100)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${completedIds.size}/${TutorialData.lessons.size} Solved",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Teal600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { completedIds.size.toFloat() / TutorialData.lessons.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Teal600,
                    trackColor = Slate200
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // HORIZONTAL PAGER FOR SWIPING LEFT AND RIGHT BETWEEN TUTORIALS
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val lesson = TutorialData.lessons[page]
            val isCurrentCompleted = completedIds.contains(lesson.id)

            // Evaluate user pattern against sample text
            val evaluationResult = remember(userPattern, lesson) {
                if (userPattern.isEmpty()) {
                    Pair(emptyList<MatchResultItem>(), false)
                } else {
                    val isPassed = lesson.validate(userPattern, lesson.sampleText)
                    val matches = runCatching {
                        val flagsSet = mutableSetOf<RegexOption>()
                        if (lesson.defaultFlags.contains("i")) flagsSet.add(RegexOption.IGNORE_CASE)
                        if (lesson.defaultFlags.contains("m")) flagsSet.add(RegexOption.MULTILINE)
                        if (lesson.defaultFlags.contains("s")) flagsSet.add(RegexOption.DOT_MATCHES_ALL)

                        val regex = Regex(userPattern.trim(), flagsSet)
                        regex.findAll(lesson.sampleText).mapIndexed { idx, m ->
                            MatchResultItem(
                                matchIndex = idx,
                                value = m.value,
                                range = m.range,
                                groups = m.groups.mapIndexedNotNull { gIdx, g ->
                                    if (g != null) com.example.model.CaptureGroup(gIdx, null, g.value, g.range) else null
                                }
                            )
                        }.toList()
                    }.getOrDefault(emptyList())

                    Pair(matches, isPassed)
                }
            }

            val matches = evaluationResult.first
            val isPassed = evaluationResult.second

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LESSON CONCEPT & GUIDE CARD
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
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(lesson.level.badgeColorHex).copy(alpha = 0.15f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = lesson.level.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(lesson.level.badgeColorHex)
                                    )
                                }

                                if (isCurrentCompleted) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = Emerald500,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Completed",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Emerald500
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = lesson.detailedGuide,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate800,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Target Challenge Goal Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Indigo50)
                                    .border(1.dp, Indigo100, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "🎯 YOUR OBJECTIVE:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo600,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = lesson.targetGoalDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate900
                                    )
                                }
                            }
                        }
                    }
                }

                // INTERACTIVE PRACTICE TEST BOX
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
                                    text = "SOLUTION REGEX PATTERN:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate600,
                                    letterSpacing = 1.sp
                                )

                                IconButton(
                                    onClick = { viewModel.toggleTutorialHint() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "Hint",
                                        tint = if (showHint) Amber500 else Slate600
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = userPattern,
                                onValueChange = { viewModel.updateTutorialPattern(it) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                placeholder = { Text("Type pattern e.g. \\d+", fontFamily = FontFamily.Monospace) },
                                leadingIcon = {
                                    Text(
                                        "/",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Teal600,
                                        modifier = Modifier.padding(start = 12.dp)
                                    )
                                },
                                trailingIcon = {
                                    Text(
                                        "/" + lesson.defaultFlags,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = Indigo600,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (isPassed) Emerald500 else Teal600,
                                    unfocusedBorderColor = Slate200,
                                    focusedContainerColor = Slate50,
                                    unfocusedContainerColor = Slate50
                                ),
                                singleLine = true
                            )

                            // Hint Card
                            AnimatedVisibility(visible = showHint) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                                    border = CardDefaults.outlinedCardBorder(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "💡 Lesson Hints:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                        lesson.hints.forEach { hintText ->
                                            Text(
                                                text = "• $hintText",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF78350F),
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // PASS / FAIL STATUS CARD
                            if (userPattern.isNotEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isPassed) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                                    ),
                                    border = CardDefaults.outlinedCardBorder(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.HelpOutline,
                                            contentDescription = null,
                                            tint = if (isPassed) Emerald500 else Rose500,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isPassed) "🎉 Challenge Mastered!" else "Keep trying...",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPassed) Color(0xFF065F46) else Color(0xFF991B1B)
                                            )
                                            Text(
                                                text = if (isPassed) "Pattern solves the challenge requirement!" else "Adjust pattern tokens to match required text.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isPassed) Color(0xFF047857) else Color(0xFFB91C1C)
                                            )
                                        }

                                        if (isPassed) {
                                            Button(
                                                onClick = {
                                                    viewModel.submitTutorialSolution()
                                                    if (page < TutorialData.lessons.size - 1) {
                                                        coroutineScope.launch {
                                                            pagerState.animateScrollToPage(page + 1)
                                                        }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("Next")
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowForward,
                                                        contentDescription = "Next Lesson",
                                                        modifier = Modifier.size(16.dp)
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

                // SAMPLE TEST STRING PREVIEW WITH HIGHLIGHTS
                item {
                    HighlightedCodeText(
                        text = lesson.sampleText,
                        matches = matches
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }

        // PAGER DOTS & NUMBER INDICATOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tutorial ${pagerState.currentPage + 1} of ${TutorialData.lessons.size}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Slate600
            )

            Spacer(modifier = Modifier.width(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(TutorialData.lessons.size) { i ->
                    val isCurrent = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isCurrent) Teal600 else Slate300)
                    )
                }
            }
        }
    }
}
