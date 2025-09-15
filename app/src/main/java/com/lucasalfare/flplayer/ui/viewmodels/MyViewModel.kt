package com.lucasalfare.flplayer.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.lucasalfare.flplayer.misc.getFileName
import com.lucasalfare.flplayer.misc.getMediaDuration

data class AppMediaItem(val uri: Uri, val name: String, val duration: String)

class MyViewModel : ViewModel() {
  private val _items = mutableStateOf<List<AppMediaItem>>(emptyList())
  val items get() = _items

  fun loadUrisToState(uris: List<Uri>, context: Context) {
    val nextItems = mutableListOf<AppMediaItem>()
    nextItems.addAll(_items.value)
    nextItems.addAll(
      uris.map { uri ->
        AppMediaItem(uri, getFileName(context, uri), getMediaDuration(context, uri))
      }
    )
    _items.value = nextItems
  }
}