package com.lucasalfare.flplayer.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lucasalfare.flplayer.ui.viewmodels.AppMediaItem
import com.lucasalfare.flplayer.ui.LocalAppViewModel
import com.lucasalfare.flplayer.ui.LocalNavController
import com.lucasalfare.flplayer.ui.viewmodels.MyViewModel
import com.lucasalfare.flplayer.ui.composables.FilePicker

@Composable
fun PlayListScreen() {
  val vm = LocalAppViewModel.current
  val ctx = LocalContext.current
  val navController = LocalNavController.current

  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(8.dp)
  ) {
    AppPlaylist(vm, navController)

    Box(modifier = Modifier.align(Alignment.BottomEnd)) {
      FilePicker { uris ->
        vm.loadUrisToState(uris, ctx)
      }
    }
  }
}

@Composable
fun AppPlaylist(vm: MyViewModel, navController: NavController) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 32.dp)
  ) {
    items(vm.items.value) { item ->
      AppPlaylistItem(item) { clickedUri ->
        navController.navigate(Screen.Player)
      }
      HorizontalDivider()
    }
  }
}

@Composable
fun AppPlaylistItem(item: AppMediaItem, onClick: (Uri) -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .clickable { onClick(item.uri) },
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(12.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .padding(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .background(Color.Blue, RoundedCornerShape(8.dp))
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
        Text(
          item.name,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(item.duration, fontSize = 12.sp, color = Color.Gray)
      }
    }
  }
}