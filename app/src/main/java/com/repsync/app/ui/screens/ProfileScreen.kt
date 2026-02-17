package com.repsync.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.repsync.app.ui.components.ProfileAvatar
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.TextOnDark
import com.repsync.app.ui.theme.TextOnDarkSecondary
import com.repsync.app.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val displayName = uiState.displayName ?: "Guest"
    val workoutCount = uiState.completedWorkoutCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
    ) {
        // Main profile card fills available space
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(BackgroundCard)
                .padding(16.dp),
        ) {
            // "Profile" header centered
            Text(
                text = "Profile",
                modifier = Modifier.fillMaxWidth(),
                color = TextOnDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Profile row: avatar, name/count, chevron
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigateToEditProfile() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Avatar (shows saved photo or placeholder icon)
                ProfileAvatar(
                    avatarPath = uiState.avatarPath,
                    size = 56.dp,
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Name and workout count
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        color = TextOnDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$workoutCount Workouts",
                        color = TextOnDarkSecondary,
                        fontSize = 16.sp,
                    )
                }

                // Chevron
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Edit profile",
                    tint = TextOnDarkSecondary,
                    modifier = Modifier.size(28.dp),
                )
            }
        }

        // Space for bottom nav
        Spacer(modifier = Modifier.height(16.dp))
    }
}
