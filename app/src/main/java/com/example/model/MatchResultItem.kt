package com.example.model

data class CaptureGroup(
    val index: Int,
    val name: String?,
    val value: String,
    val range: IntRange
)

data class MatchResultItem(
    val matchIndex: Int,
    val value: String,
    val range: IntRange,
    val groups: List<CaptureGroup>
)
