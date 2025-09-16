package com.lucasalfare.flplayer.ui.screens

import android.app.Activity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.ui.PlayerView
import com.lucasalfare.flplayer.ui.LocalAppViewModel
import com.lucasalfare.flplayer.ui.viewmodels.AppMediaItem
import com.lucasalfare.flplayer.ui.viewmodels.ExoPlayerViewModel
import kotlinx.coroutines.delay

// PlayerScreen.kt
@Composable
fun PlayerScreen(playerVm: ExoPlayerViewModel) {
  val context = LocalContext.current
  val appVm = LocalAppViewModel.current
  val items = appVm.items.value
  if (items.isEmpty()) return

  val window = (context as Activity).window

  var controlsVisible by remember { mutableStateOf(true) }

  LaunchedEffect(playerVm.exoPlayer) {
    if (playerVm.exoPlayer.mediaItemCount == 0) {
      playerVm.setItems(items, autoPlay = true)
    }
  }

  LaunchedEffect(controlsVisible) {
    if (controlsVisible) {
      delay(3000)
      controlsVisible = false
    }
  }

  DisposableEffect(Unit) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior =
      WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    onDispose {
      WindowCompat.setDecorFitsSystemWindows(window, true)
      controller.show(WindowInsetsCompat.Type.systemBars())
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectTapGestures { controlsVisible = !controlsVisible }
      }
  ) {
    AndroidView(
      factory = { ctx ->
        PlayerView(ctx).apply {
          player = playerVm.exoPlayer
          useController = false
          keepScreenOn = true
          layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
      PlayerControls(
        playerVm = playerVm,
        items = items,
        controlsVisible = controlsVisible
      )
    }
  }
}

@Composable
fun PlayerControls(
  playerVm: ExoPlayerViewModel,
  items: List<AppMediaItem>,
  controlsVisible: Boolean
) {
  AnimatedVisibility(
    visible = controlsVisible,
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        items.getOrNull(playerVm.currentIndex.intValue)?.name ?: "",
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      Slider(
        value = playerVm.sliderPosition.floatValue,
        onValueChange = { playerVm.seekTo(it) },
        colors = SliderDefaults.colors(
          thumbColor = Color.White,
          activeTrackColor = Color.White.copy(alpha = 0.7f),
          inactiveTrackColor = Color.White.copy(alpha = 0.3f)
        ),
        modifier = Modifier.height(4.dp)
      )
      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(
          onClick = { playerVm.skipPrevious() },
          enabled = playerVm.currentIndex.intValue > 0
        ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White) }

        IconButton(onClick = { playerVm.togglePlayPause() }) {
          Icon(
            if (playerVm.isPlaying.value) Icons.Default.MoreVert else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White
          )
        }

        IconButton(
          onClick = { playerVm.skipNext() },
          enabled = playerVm.currentIndex.intValue < items.lastIndex
        ) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White) }
      }
    }
  }
}