package com.lucasalfare.flplayer.ui.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilePicker(
  onFilesSelected: (List<Uri>) -> Unit
) {
  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenMultipleDocuments(),
    onResult = { uris -> if (uris.isNotEmpty()) onFilesSelected(uris) }
  )

  FloatingActionButton(
    onClick = { launcher.launch(arrayOf("audio/*", "video/*")) },
    shape = CircleShape,
    modifier = Modifier.padding(12.dp)
  ) {
    Icon(
      imageVector = Icons.Default.Add,
      contentDescription = "Select and add files"
    )
  }
}