package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MatchResultItem
import com.example.ui.theme.MatchHighlights
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Teal600

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HighlightedCodeText(
    text: String,
    matches: List<MatchResultItem>,
    selectedMatchIndex: Int? = null,
    onMatchClick: ((MatchResultItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val annotatedString = buildAnnotatedString {
        append(text)

        // Sort matches by start position to prevent overlapping issues
        val sortedMatches = matches.sortedBy { it.range.first }

        sortedMatches.forEachIndexed { idx, match ->
            val start = match.range.first.coerceIn(0, text.length)
            val end = (match.range.last + 1).coerceIn(0, text.length)

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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MATCH HIGHLIGHT OVERLAY",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (matches.isNotEmpty()) Teal600 else Slate300)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${matches.size} match${if (matches.size != 1) "es" else ""}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (matches.isNotEmpty()) Teal600 else Slate600
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            SelectionContainer {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate100)
                        .padding(12.dp)
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = "Enter test string above to preview real-time regex match highlights...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate600,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        Text(
                            text = annotatedString,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = Slate900
                        )
                    }
                }
            }

            if (matches.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Quick Match Chips:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    matches.take(12).forEachIndexed { idx, match ->
                        val colorPair = MatchHighlights[idx % MatchHighlights.size]
                        val isSelected = selectedMatchIndex == match.matchIndex

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) colorPair.border else colorPair.bg)
                                .border(1.dp, colorPair.border, RoundedCornerShape(20.dp))
                                .clickable { onMatchClick?.invoke(match) }
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
                    if (matches.size > 12) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Slate200)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+${matches.size - 12} more",
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
