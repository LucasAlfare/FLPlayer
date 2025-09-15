package com.lucasalfare.flplayer.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ExoPlayerViewModel.kt
class ExoPlayerViewModel(app: Application) : AndroidViewModel(app) {
  private val _currentIndex = mutableIntStateOf(0)
  private val _isPlaying = mutableStateOf(false)
  private val _sliderPosition = mutableFloatStateOf(0f)

  val currentIndex get() = _currentIndex
  val isPlaying get() = _isPlaying
  val sliderPosition get() = _sliderPosition

  val exoPlayer: ExoPlayer = ExoPlayer.Builder(app).build()

  init {
    viewModelScope.launch {
      while (true) {
        val duration = exoPlayer.duration.takeIf { it > 0 } ?: 1L
        _sliderPosition.floatValue = exoPlayer.currentPosition.toFloat() / duration
        delay(100L)
      }
    }
    exoPlayer.addListener(object : Player.Listener {
      override fun onPlaybackStateChanged(state: Int) {
        if (state == Player.STATE_ENDED) {
          if (_currentIndex.intValue < exoPlayer.mediaItemCount - 1) {
            _currentIndex.intValue++
            exoPlayer.seekToDefaultPosition(_currentIndex.intValue)
            exoPlayer.play()
          } else {
            exoPlayer.seekTo(0)
            _isPlaying.value = false
          }
        }
      }
    })
  }

  fun setItems(items: List<AppMediaItem>, autoPlay: Boolean = false) {
    exoPlayer.setMediaItems(items.map { MediaItem.fromUri(it.uri) })
    exoPlayer.prepare()
    if (autoPlay) play()
  }

  fun play() {
    _isPlaying.value = true; exoPlayer.play()
  }

  fun pause() {
    _isPlaying.value = false; exoPlayer.pause()
  }

  fun togglePlayPause() {
    if (_isPlaying.value) pause() else play()
  }

  fun seekTo(position: Float) {
    exoPlayer.seekTo((position * exoPlayer.duration).toLong())
  }

  fun skipNext() {
    if (_currentIndex.intValue < exoPlayer.mediaItemCount - 1) {
      _currentIndex.intValue++
      exoPlayer.seekToDefaultPosition(_currentIndex.intValue)
      if (_isPlaying.value) exoPlayer.play()
    }
  }

  fun skipPrevious() {
    if (_currentIndex.intValue > 0) {
      _currentIndex.intValue--
      exoPlayer.seekToDefaultPosition(_currentIndex.intValue)
      if (_isPlaying.value) exoPlayer.play()
    }
  }

  fun finish() {
    exoPlayer.release()
  }

  override fun onCleared() {
    finish()
  }
}