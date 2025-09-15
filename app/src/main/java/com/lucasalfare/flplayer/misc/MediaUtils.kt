package com.lucasalfare.flplayer.misc

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private const val MINUTES_PATTERN = "mm:ss"
private const val HOURS_PATTERN = "hh:mm:ss"

fun getMediaDuration(context: Context, uri: Uri): String = try {
  val mmr = MediaMetadataRetriever().apply { setDataSource(context, uri) }
  val mediaDurationMs =
    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L

  val nextPattern = if (mediaDurationMs < (60 * 60 * 1000)) MINUTES_PATTERN else HOURS_PATTERN
  val formatter = SimpleDateFormat(nextPattern, Locale.getDefault())
  formatter.timeZone = TimeZone.getTimeZone("UTC")
  formatter.format(mediaDurationMs)
} catch (_: Exception) {
  "--:--"
}

fun getFileName(context: Context, uri: Uri): String {
  return when {
    uri.scheme == "content" -> {
      context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          .takeIf { it >= 0 && cursor.moveToFirst() }
          ?.let { cursor.getString(it) }
      }
    }

    else -> uri.path?.substringAfterLast('/')
  } ?: "Unknown"
}