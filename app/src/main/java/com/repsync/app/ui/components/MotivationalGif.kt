package com.repsync.app.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.repsync.app.ui.theme.BackgroundCard
import com.repsync.app.ui.theme.TextOnDarkSecondary
import java.time.LocalDate

/**
 * Curated list of funny workout / motivational GIF URLs.
 * Uses direct media links for reliability.
 */
private val workoutGifs = listOf(
    "https://media.giphy.com/media/3oKIPavRPgJYaNI97W/giphy.gif",
    "https://media.giphy.com/media/ZaKcIYMjNYMKjbBp1o/giphy.gif",
    "https://media.giphy.com/media/l0HUbtILos6CfpevC/giphy.gif",
    "https://media.giphy.com/media/YqbByMOb8cPbMzgLbf/giphy.gif",
    "https://media.giphy.com/media/5GoVLqeAOo6PK/giphy.gif",
    "https://media.giphy.com/media/KzM1lAfJjCWNq/giphy.gif",
    "https://media.giphy.com/media/WUlplcMpOCEmTGBtBW/giphy.gif",
    "https://media.giphy.com/media/Vccpm1O9gV1g4/giphy.gif",
    "https://media.giphy.com/media/3ohhwoWSCtJzznXbuo/giphy.gif",
    "https://media.giphy.com/media/l46CyJmS9KUbokzsI/giphy.gif",
    "https://media.giphy.com/media/j3IxJRLNLZz9sXR7ZA/giphy.gif",
    "https://media.giphy.com/media/xUOxfoA5ffZ8xoTDC8/giphy.gif",
    "https://media.giphy.com/media/xT0xeuOy2Fcl9vDGiA/giphy.gif",
    "https://media.giphy.com/media/CjmvTCZf2U3p09Cn0h/giphy.gif",
    "https://media.giphy.com/media/kaBmqpJMOEJGM/giphy.gif",
)

/**
 * Funny fallback emoji combos shown when GIF can't load (no internet).
 */
private val fallbackEmojis = listOf(
    "\uD83D\uDCAA\uD83D\uDE24",       // flexing + determined
    "\uD83C\uDFCB\uFE0F\u200D\u2642\uFE0F\uD83D\uDD25", // weightlifter + fire
    "\uD83E\uDDB5\uD83D\uDCAA",       // leg + flex
    "\uD83C\uDFC3\u200D\u2642\uFE0F\uD83D\uDCA8", // running + dash
    "\uD83E\uDD3C\u200D\u2642\uFE0F\uD83D\uDE04", // wrestler + grin
    "\uD83E\uDDD8\u200D\u2642\uFE0F\u2728", // meditating + sparkle
    "\uD83C\uDFCB\uFE0F\u200D\u2640\uFE0F\uD83D\uDCAF", // weightlifter + 100
)

/**
 * Displays a random funny workout GIF that changes every day.
 * Falls back to emoji if offline.
 */
@Composable
fun MotivationalGif(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var loadFailed by remember { mutableStateOf(false) }

    val dayOfYear = remember { LocalDate.now().dayOfYear }
    val gifUrl = remember { workoutGifs[dayOfYear % workoutGifs.size] }

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BackgroundCard),
        contentAlignment = Alignment.Center,
    ) {
        if (loadFailed) {
            // Offline fallback — show a fun emoji + hint
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = fallbackEmojis[dayOfYear % fallbackEmojis.size],
                    fontSize = 48.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Connect to internet for daily GIFs!",
                    color = TextOnDarkSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(gifUrl)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Daily motivational GIF",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.FillWidth,
                onState = { state ->
                    if (state is AsyncImagePainter.State.Error) {
                        loadFailed = true
                    }
                },
            )
        }
    }
}
