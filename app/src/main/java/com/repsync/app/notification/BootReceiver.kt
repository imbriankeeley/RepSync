package com.repsync.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.repsync.app.data.ReminderPreferences
import com.repsync.app.data.WorkoutDaysPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val reminderPrefs = ReminderPreferences.getInstance(context)
        val workoutDaysPrefs = WorkoutDaysPreferences.getInstance(context)
        val scheduler = ReminderScheduler(context)

        CoroutineScope(Dispatchers.IO).launch {
            val enabled = reminderPrefs.enabled.first()
            if (enabled) {
                val days = workoutDaysPrefs.days.first()
                val hour = reminderPrefs.hour.first()
                val minute = reminderPrefs.minute.first()
                if (days.isNotEmpty()) {
                    scheduler.scheduleReminders(days, hour, minute)
                }
            }
        }
    }
}
