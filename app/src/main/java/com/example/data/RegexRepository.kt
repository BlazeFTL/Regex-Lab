package com.example.data

import kotlinx.coroutines.flow.Flow

class RegexRepository(private val regexDao: RegexDao) {
    val savedPatterns: Flow<List<SavedPatternEntity>> = regexDao.getAllSavedPatterns()
    val tutorialProgress: Flow<List<TutorialProgressEntity>> = regexDao.getAllTutorialProgress()

    suspend fun savePattern(pattern: SavedPatternEntity): Long {
        return regexDao.insertSavedPattern(pattern)
    }

    suspend fun deletePattern(id: Long) {
        regexDao.deleteSavedPattern(id)
    }

    suspend fun markLessonCompleted(lessonId: String, userPattern: String) {
        regexDao.saveTutorialProgress(
            TutorialProgressEntity(
                lessonId = lessonId,
                isCompleted = true,
                userPattern = userPattern,
                completedAt = System.currentTimeMillis()
            )
        )
    }
}
