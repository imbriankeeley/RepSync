package com.repsync.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek

private val Context.workoutDaysDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "workout_days_prefs"
)

class WorkoutDaysPreferences(private val context: Context) {

    companion object {
        private val KEY_DAYS = stringPreferencesKey("workout_days")

        @Volatile
        private var INSTANCE: WorkoutDaysPreferences? = null

        fun getInstance(context: Context): WorkoutDaysPreferences {
            return INSTANCE ?: synchronized(this) {
                WorkoutDaysPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val days: Flow<Set<DayOfWeek>> = context.workoutDaysDataStore.data
        .map { prefs ->
            val raw = prefs[KEY_DAYS] ?: ""
            if (raw.isEmpty()) emptySet()
            else raw.split(",").mapNotNull { it.toIntOrNull()?.let { v -> DayOfWeek.of(v) } }.toSet()
        }

    suspend fun setDays(days: Set<DayOfWeek>) {
        context.workoutDaysDataStore.edit { prefs ->
            prefs[KEY_DAYS] = days.joinToString(",") { it.value.toString() }
        }
    }
}
