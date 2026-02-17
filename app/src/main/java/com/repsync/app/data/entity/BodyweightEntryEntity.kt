package com.repsync.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bodyweight_entries")
data class BodyweightEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val weight: Double, // lbs
)
