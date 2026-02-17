package com.repsync.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek

private val Context.reminderDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "reminder_prefs"
)

class ReminderPreferences(private val context: Context) {

    companion object {
        private val KEY_ENABLED = booleanPreferencesKey("reminder_enabled")
        private val KEY_DAYS = stringPreferencesKey("reminder_days")
        private val KEY_HOUR = intPreferencesKey("reminder_hour")
        private val KEY_MINUTE = intPreferencesKey("reminder_minute")
        private val KEY_MESSAGE = stringPreferencesKey("reminder_message")

        const val DEFAULT_HOUR = 18 // 6 PM
        const val DEFAULT_MINUTE = 0
        const val DEFAULT_MESSAGE = "Time to Work Out!"

        @Volatile
        private var INSTANCE: ReminderPreferences? = null

        fun getInstance(context: Context): ReminderPreferences {
            return INSTANCE ?: synchronized(this) {
                ReminderPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val enabled: Flow<Boolean> = context.reminderDataStore.data
        .map { prefs -> prefs[KEY_ENABLED] ?: false }

    val days: Flow<Set<DayOfWeek>> = context.reminderDataStore.data
        .map { prefs ->
            val raw = prefs[KEY_DAYS] ?: ""
            if (raw.isEmpty()) emptySet()
            else raw.split(",").mapNotNull { it.toIntOrNull()?.let { v -> DayOfWeek.of(v) } }.toSet()
        }

    val hour: Flow<Int> = context.reminderDataStore.data
        .map { prefs -> prefs[KEY_HOUR] ?: DEFAULT_HOUR }

    val minute: Flow<Int> = context.reminderDataStore.data
        .map { prefs -> prefs[KEY_MINUTE] ?: DEFAULT_MINUTE }

    val message: Flow<String> = context.reminderDataStore.data
        .map { prefs -> prefs[KEY_MESSAGE] ?: DEFAULT_MESSAGE }

    suspend fun setEnabled(enabled: Boolean) {
        context.reminderDataStore.edit { prefs ->
            prefs[KEY_ENABLED] = enabled
        }
    }

    suspend fun setDays(days: Set<DayOfWeek>) {
        context.reminderDataStore.edit { prefs ->
            prefs[KEY_DAYS] = days.joinToString(",") { it.value.toString() }
        }
    }

    suspend fun setHour(hour: Int) {
        context.reminderDataStore.edit { prefs ->
            prefs[KEY_HOUR] = hour
        }
    }

    suspend fun setMinute(minute: Int) {
        context.reminderDataStore.edit { prefs ->
            prefs[KEY_MINUTE] = minute
        }
    }

    suspend fun setMessage(message: String) {
        context.reminderDataStore.edit { prefs ->
            prefs[KEY_MESSAGE] = message
        }
    }
}
