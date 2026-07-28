package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_patterns")
data class SavedPatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val pattern: String,
    val flags: String,
    val testString: String,
    val replaceString: String = "",
    val category: String = "Custom",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tutorial_progress")
data class TutorialProgressEntity(
    @PrimaryKey
    val lessonId: String,
    val isCompleted: Boolean = true,
    val userPattern: String = "",
    val completedAt: Long = System.currentTimeMillis()
)
