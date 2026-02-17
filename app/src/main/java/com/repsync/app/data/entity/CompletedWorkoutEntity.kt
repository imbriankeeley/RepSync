package com.repsync.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_workouts")
data class CompletedWorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val templateId: Long? = null,
    val date: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    val isQuickWorkout: Boolean = false
)
