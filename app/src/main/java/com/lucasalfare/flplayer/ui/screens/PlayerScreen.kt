package com.lucasalfare.flplayer.ui.screens

import android.app.Activity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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

    PlayerControls(
      playerVm = playerVm,
      items = items,
      controlsVisible = controlsVisible,
      onToggleControls = { controlsVisible = !controlsVisible }
    )
  }
}

@Composable
fun PlayerControls(
  playerVm: ExoPlayerViewModel,
  items: List<AppMediaItem>,
  controlsVisible: Boolean,
  onToggleControls: () -> Unit
) {
  Box(modifier = Modifier.fillMaxSize()) {
    AnimatedVisibility(
      visible = controlsVisible,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .padding(8.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.LightGray.copy(alpha = .3f))
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Text(
          items.getOrNull(playerVm.currentIndex.intValue)?.name ?: "Unknown"
        )
        Slider(
          value = playerVm.sliderPosition.floatValue,
          onValueChange = { playerVm.seekTo(it) },
          onValueChangeFinished = { /* já tratado em seekTo */ }
        )
        Row {
          Button(
            onClick = { playerVm.skipPrevious() },
            enabled = playerVm.currentIndex.intValue > 0
          ) { Text("<-") }

          Button(onClick = { playerVm.togglePlayPause() }) {
            Text(if (playerVm.isPlaying.value) "Pause" else "Play")
          }

          Button(
            onClick = { playerVm.skipNext() },
            enabled = playerVm.currentIndex.intValue < items.lastIndex
          ) { Text("->") }
        }
      }
    }
  }
}