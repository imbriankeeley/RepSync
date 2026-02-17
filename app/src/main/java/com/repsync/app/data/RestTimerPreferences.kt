package com.repsync.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.restTimerDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "rest_timer_prefs"
)

class RestTimerPreferences(private val context: Context) {

    companion object {
        private val KEY_DURATION = intPreferencesKey("rest_timer_duration_seconds")
        const val DEFAULT_DURATION_SECONDS = 60

        @Volatile
        private var INSTANCE: RestTimerPreferences? = null

        fun getInstance(context: Context): RestTimerPreferences {
            return INSTANCE ?: synchronized(this) {
                RestTimerPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val durationSeconds: Flow<Int> = context.restTimerDataStore.data
        .map { prefs -> prefs[KEY_DURATION] ?: DEFAULT_DURATION_SECONDS }

    suspend fun setDuration(seconds: Int) {
        context.restTimerDataStore.edit { prefs ->
            prefs[KEY_DURATION] = seconds
        }
    }
}
