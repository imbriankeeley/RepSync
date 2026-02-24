package com.repsync.app.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

object RestTimerState {
    val secondsRemaining = MutableStateFlow(0)
    val timerCompleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val isRunning = MutableStateFlow(false)
}
