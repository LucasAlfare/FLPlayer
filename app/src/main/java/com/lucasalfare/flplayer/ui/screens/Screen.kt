package com.lucasalfare.flplayer.ui.screens

import kotlinx.serialization.Serializable

sealed class Screen {
  @Serializable
  object Playlist : Screen()
  @Serializable
  object Player : Screen()
}