package com.repsync.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.TextOnDark

@Composable
fun StreakBadge(streak: Int) {
    if (streak <= 0) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "\uD83D\uDD25",
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (streak == 1) "1 Day Streak" else "$streak Day Streak",
            color = TextOnDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
