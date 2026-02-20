package com.mhss.app.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.mhss.app.database.entity.DiaryEntryEntity
import com.mhss.app.database.entity.DiaryListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Query("SELECT id, title, SUBSTR(content, 1, 150) AS contentPreview, created_date, updated_date, mood FROM diary")
    fun getAllEntries(): Flow<List<DiaryListItem>>

    @Query("SELECT * FROM diary")
    suspend fun getAllFullEntries(): List<DiaryEntryEntity>

    @Query("SELECT * FROM diary WHERE id = :id")
    suspend fun getEntry(id: String): DiaryEntryEntity?

    @Query("SELECT id, title, SUBSTR(content, 1, 100) AS contentPreview, created_date, updated_date, mood FROM diary WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    suspend fun getEntriesByTitle(query: String): List<DiaryListItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(diary: DiaryEntryEntity)

    @Upsert
    suspend fun upsertEntries(diary: List<DiaryEntryEntity>)

    @Update
    suspend fun updateEntry(diary: DiaryEntryEntity)

    @Delete
    suspend fun deleteEntry(diary: DiaryEntryEntity)

}