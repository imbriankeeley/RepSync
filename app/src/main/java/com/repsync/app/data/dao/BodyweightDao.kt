package com.repsync.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.repsync.app.data.entity.BodyweightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyweightDao {

    @Insert
    suspend fun insert(entry: BodyweightEntryEntity)

    @Delete
    suspend fun delete(entry: BodyweightEntryEntity)

    @Query("SELECT * FROM bodyweight_entries ORDER BY date ASC")
    fun getAllEntriesChronological(): Flow<List<BodyweightEntryEntity>>

    @Query("SELECT * FROM bodyweight_entries ORDER BY date DESC LIMIT 1")
    fun getLatestEntry(): Flow<BodyweightEntryEntity?>
}
