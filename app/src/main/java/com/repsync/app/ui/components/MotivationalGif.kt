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

/**
 * Curated list of anime motivational GIF URLs.
 * Uses direct media links for reliability. New random GIF each app open.
 */
private val workoutGifs = listOf(
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMHNqeXd3am00MzV1aDltaHlxNXk0enk5dWpsdW52cXZ5MmQ1cXdibSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/fqrXU5bfnbQg9bCAKI/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExcTBjYmRsdmMyY2xic2traTJwZ2Nub20ydGdtc2RsdndhbzE5bGRwMCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/rzHpW6vWZX3ghRP2Cc/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExZHE0eGFnODhkdDV0amRkejNlMnZ2amxqYzNqazNiczhxOWdkeWdxZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/12bF3AWU423YeA/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExZHE0eGFnODhkdDV0amRkejNlMnZ2amxqYzNqazNiczhxOWdkeWdxZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/ktDkLA5Hp9dM4/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExZHE0eGFnODhkdDV0amRkejNlMnZ2amxqYzNqazNiczhxOWdkeWdxZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/6KfOhA4rh822RS0Phn/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExZHE0eGFnODhkdDV0amRkejNlMnZ2amxqYzNqazNiczhxOWdkeWdxZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/l0Iymo6MEqAJfHaso/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3YWVodjl6N28zbGpiamcwOWg4MzJ2NGJldTI4dHl5Nm9tcmJsNzlmaiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/YZX4FWwOJTK5W/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExcGoxaG5qM3Q0MnU4b3J4YnR2dTE3cm4xaDA0dWs4eXdycjltbHk3ZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/gKc0n2MdnezJK/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMWh2d2FpeWMxcDAzcnEwa3Q2eHRzbXJrbDFhbTNicm5oZThhdWRkZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/jleNxE9BsJVO8/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMWh2d2FpeWMxcDAzcnEwa3Q2eHRzbXJrbDFhbTNicm5oZThhdWRkZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/VFBAJmjmArR6jcWr9G/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMWh2d2FpeWMxcDAzcnEwa3Q2eHRzbXJrbDFhbTNicm5oZThhdWRkZiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/qW3iR9I30ndCM/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3bzB0Z29pc3h5eW01bTFqajVpc3IweGxydmQ4b3FlcWNwNnlna3BtNiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/9jNjhwA5S4zMQ/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaml0bHA2OTlwc2hwOXBtYTczOWhobmJmNzNuM3R0N3hmcDVxbTJ3OCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/54R23E45hwjrMrj450/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaml0bHA2OTlwc2hwOXBtYTczOWhobmJmNzNuM3R0N3hmcDVxbTJ3OCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/pRgMFB4CxBAILSZsLH/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaml0bHA2OTlwc2hwOXBtYTczOWhobmJmNzNuM3R0N3hmcDVxbTJ3OCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/UejTmpSCJ0ikb2lXWS/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaml0bHA2OTlwc2hwOXBtYTczOWhobmJmNzNuM3R0N3hmcDVxbTJ3OCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/enRj5D72igadpxSYDM/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaml0bHA2OTlwc2hwOXBtYTczOWhobmJmNzNuM3R0N3hmcDVxbTJ3OCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/JRX5tDQRpe3uI45az0/giphy.gif",
)

/**
 * Anime-themed fallback emoji combos shown when GIF can't load (no internet).
 */
private val fallbackEmojis = listOf(
    "\u2728\uD83C\uDF38",             // sparkle + cherry blossom
    "\uD83D\uDCAB\uD83C\uDF19",       // dizzy + crescent moon
    "\uD83C\uDF1F\uD83C\uDF80",       // star + ribbon
    "\uD83D\uDD25\u2764\uFE0F",       // fire + heart
    "\uD83C\uDF38\uD83D\uDCAA",       // cherry blossom + flex
    "\u2B50\uD83C\uDF1F",             // star + glowing star
    "\uD83E\uDD0D\u2728",             // white heart + sparkle
)

/**
 * Displays a random funny workout GIF that changes every day.
 * Falls back to emoji if offline.
 */
@Composable
fun MotivationalGif(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var loadFailed by remember { mutableStateOf(false) }

    val gifUrl = remember { workoutGifs.random() }

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
                    text = fallbackEmojis.random(),
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
