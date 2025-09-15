package com.lucasalfare.flplayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lucasalfare.flplayer.ui.screens.PlayListScreen
import com.lucasalfare.flplayer.ui.screens.PlayerScreen
import com.lucasalfare.flplayer.ui.screens.Screen
import com.lucasalfare.flplayer.ui.viewmodels.ExoPlayerViewModel
import com.lucasalfare.flplayer.ui.viewmodels.MyViewModel

val LocalAppViewModel = staticCompositionLocalOf<MyViewModel> { error("No state") }
val LocalNavController = staticCompositionLocalOf<NavHostController> { error("no controller!") }

@Composable
fun App() {
  val navigationController = rememberNavController()
  val appVm: MyViewModel = viewModel()

  CompositionLocalProvider(
    LocalAppViewModel provides appVm,
    LocalNavController provides navigationController
  ) {
    NavHost(navController = navigationController, startDestination = Screen.Playlist) {
      composable<Screen.Playlist> { PlayListScreen() }
      composable<Screen.Player> { backStackEntry ->
        val playerVm: ExoPlayerViewModel = viewModel()
        PlayerScreen(playerVm)
      }
    }
  }
}