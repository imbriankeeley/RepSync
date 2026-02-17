package com.repsync.app.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.repsync.app.data.RepSyncDatabase
import com.repsync.app.data.entity.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class ProfileUiState(
    val displayName: String? = null,
    val avatarPath: String? = null,
    val completedWorkoutCount: Int = 0,
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = RepSyncDatabase.getDatabase(application)
    private val userProfileDao = db.userProfileDao()
    private val completedWorkoutDao = db.completedWorkoutDao()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
        observeWorkoutCount()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            userProfileDao.getProfile().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    displayName = profile?.displayName,
                    avatarPath = profile?.avatarPath,
                )
            }
        }
    }

    private fun observeWorkoutCount() {
        viewModelScope.launch {
            completedWorkoutDao.getCompletedWorkoutCount().collect { count ->
                _uiState.value = _uiState.value.copy(
                    completedWorkoutCount = count
                )
            }
        }
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            val trimmed = name.trim()
            val current = userProfileDao.getProfileOnce()
            val profile = UserProfileEntity(
                id = 1,
                displayName = trimmed.ifEmpty { null },
                avatarPath = current?.avatarPath,
            )
            userProfileDao.upsertProfile(profile)
        }
    }

    fun updateAvatar(uri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            val avatarFile = File(context.filesDir, "profile_avatar.jpg")
            try {
                withContext(Dispatchers.IO) {
                    // Copy the raw file first
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        avatarFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Read EXIF orientation and correct if needed
                    val exif = ExifInterface(avatarFile.absolutePath)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL,
                    )

                    val rotation = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }

                    if (rotation != 0f) {
                        val original = BitmapFactory.decodeFile(avatarFile.absolutePath)
                        if (original != null) {
                            val matrix = Matrix().apply { postRotate(rotation) }
                            val rotated = Bitmap.createBitmap(
                                original, 0, 0,
                                original.width, original.height,
                                matrix, true,
                            )
                            avatarFile.outputStream().use { out ->
                                rotated.compress(Bitmap.CompressFormat.JPEG, 90, out)
                            }
                            if (rotated !== original) rotated.recycle()
                            original.recycle()
                        }
                    }
                }

                val current = userProfileDao.getProfileOnce()
                val profile = UserProfileEntity(
                    id = 1,
                    displayName = current?.displayName,
                    avatarPath = avatarFile.absolutePath,
                )
                userProfileDao.upsertProfile(profile)
            } catch (_: Exception) {
                // Silently fail if image processing fails
            }
        }
    }
}
