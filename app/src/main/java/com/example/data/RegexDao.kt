package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegexDao {
    @Query("SELECT * FROM saved_patterns ORDER BY timestamp DESC")
    fun getAllSavedPatterns(): Flow<List<SavedPatternEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPattern(pattern: SavedPatternEntity): Long

    @Query("DELETE FROM saved_patterns WHERE category = 'History' AND id NOT IN (SELECT MAX(id) FROM saved_patterns WHERE category = 'History' GROUP BY pattern, flags, testString, replaceString)")
    suspend fun deleteDuplicateHistory()

    @Query("DELETE FROM saved_patterns WHERE id = :id")
    suspend fun deleteSavedPattern(id: Long)

    @Query("DELETE FROM saved_patterns")
    suspend fun clearAllSavedPatterns()

    @Query("SELECT * FROM tutorial_progress")
    fun getAllTutorialProgress(): Flow<List<TutorialProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTutorialProgress(progress: TutorialProgressEntity)
}
