package com.repsync.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.repsync.app.data.converter.Converters
import com.repsync.app.data.dao.CompletedWorkoutDao
import com.repsync.app.data.dao.UserProfileDao
import com.repsync.app.data.dao.WorkoutDao
import com.repsync.app.data.entity.CompletedExerciseEntity
import com.repsync.app.data.entity.CompletedSetEntity
import com.repsync.app.data.entity.CompletedWorkoutEntity
import com.repsync.app.data.entity.ExerciseEntity
import com.repsync.app.data.entity.ExerciseSetEntity
import com.repsync.app.data.entity.UserProfileEntity
import com.repsync.app.data.entity.WorkoutEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        ExerciseSetEntity::class,
        CompletedWorkoutEntity::class,
        CompletedExerciseEntity::class,
        CompletedSetEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RepSyncDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun completedWorkoutDao(): CompletedWorkoutDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: RepSyncDatabase? = null

        fun getDatabase(context: Context): RepSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RepSyncDatabase::class.java,
                    "repsync_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
