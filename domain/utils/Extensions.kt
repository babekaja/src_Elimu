package com.kotlingdgocucb.elimuApp.domain.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Flow Extensions
fun <T> Flow<T>.asUiState(): Flow<UiState<T>> {
    return this
        .map<T, UiState<T>> { UiState.Success(it) }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
}

fun <T> Flow<List<T>>.asUiStateWithEmpty(): Flow<UiState<List<T>>> {
    return this
        .map<List<T>, UiState<List<T>>> { 
            if (it.isEmpty()) UiState.Empty else UiState.Success(it) 
        }
        .onStart { emit(UiState.Loading) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
}

// Composable Extensions
@Composable
fun UiState<*>.HandleError(
    onError: (String) -> Unit = { message ->
        LocalContext.current.showToast(message)
    }
) {
    LaunchedEffect(this) {
        if (this@HandleError is UiState.Error) {
            onError(message)
        }
    }
}

// String Extensions
fun String.toYouTubeUrl(): String {
    return "${Constants.YOUTUBE_WATCH_BASE_URL}$this"
}

fun String.toYouTubeThumbnail(quality: String = "hqdefault"): String {
    return "${Constants.YOUTUBE_THUMBNAIL_BASE_URL}/$this/$quality.jpg"
}

fun String.toYouTubeEmbed(autoplay: Boolean = false): String {
    val autoplayParam = if (autoplay) "?autoplay=1" else ""
    return "${Constants.YOUTUBE_EMBED_BASE_URL}/$this$autoplayParam"
}

// Number Extensions
fun Int.toFormattedString(): String {
    return when {
        this >= 1_000_000 -> "${this / 1_000_000}M"
        this >= 1_000 -> "${this / 1_000}K"
        else -> this.toString()
    }
}

// Collection Extensions
fun <T> List<T>.chunkedSafely(size: Int): List<List<T>> {
    return if (isEmpty()) emptyList() else chunked(size)
}
</btml:function_calls>