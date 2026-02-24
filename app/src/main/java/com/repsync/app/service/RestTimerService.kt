package com.repsync.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.repsync.app.R

class RestTimerService : Service() {

    companion object {
        const val EXTRA_DURATION_SECONDS = "EXTRA_DURATION_SECONDS"
        const val ACTION_STOP = "ACTION_STOP"
        private const val CHANNEL_ID = "rest_timer_channel"
        private const val NOTIFICATION_ID = 1001
    }

    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopTimer()
            stopSelf()
            return START_NOT_STICKY
        }

        val durationSeconds = intent?.getIntExtra(EXTRA_DURATION_SECONDS, 60) ?: 60

        // Cancel any existing timer
        countDownTimer?.cancel()

        RestTimerState.isRunning.value = true
        RestTimerState.secondsRemaining.value = durationSeconds

        // Start foreground with initial notification
        startForeground(NOTIFICATION_ID, buildNotification(durationSeconds))

        countDownTimer = object : CountDownTimer(durationSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = ((millisUntilFinished + 500) / 1000).toInt()
                RestTimerState.secondsRemaining.value = remaining
                updateNotification(remaining)
            }

            override fun onFinish() {
                RestTimerState.secondsRemaining.value = 0
                onTimerComplete()
            }
        }.start()

        return START_NOT_STICKY
    }

    private fun onTimerComplete() {
        playAlarmSound()
        triggerVibration()
        RestTimerState.timerCompleted.tryEmit(Unit)
        RestTimerState.isRunning.value = false

        // Stop service after alarm finishes (1200ms + buffer)
        handler.postDelayed({
            stopSelf()
        }, 1500L)
    }

    private fun playAlarmSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@RestTimerService, alarmUri)
                val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                val targetVolume = (maxVolume * 0.7).toInt().coerceAtLeast(1)
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, targetVolume, 0)
                prepare()
                start()
            }

            // Stop after 1200ms
            handler.postDelayed({
                stopAlarmSound()
            }, 1200L)
        } catch (_: Exception) {
            // Alarm sound may not be available on some devices
        }
    }

    private fun stopAlarmSound() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {
            // Ignore cleanup errors
        }
        mediaPlayer = null
    }

    private fun triggerVibration() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            val pattern = longArrayOf(0, 400, 200, 400, 200, 400, 200, 400)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } catch (_: Exception) {
            // Vibration may not be available
        }
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        stopAlarmSound()
        RestTimerState.secondsRemaining.value = 0
        RestTimerState.isRunning.value = false
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Rest Timer",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Shows rest timer countdown"
            setSound(null, null)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(secondsRemaining: Int): android.app.Notification {
        val stopIntent = Intent(this, RestTimerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Rest Timer")
            .setContentText("${secondsRemaining}s remaining")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setSilent(true)
            .addAction(0, "Skip", stopPendingIntent)
            .build()
    }

    private fun updateNotification(secondsRemaining: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(secondsRemaining))
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        countDownTimer = null
        stopAlarmSound()
        handler.removeCallbacksAndMessages(null)
    }
}
