package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

object ProfilePhotoManager {

  /**
   * Generates a temporary camera capture URI backed by FileProvider.
   */
  fun createCameraTempUri(context: Context): Pair<Uri, File> {
    val imagesDir = File(context.cacheDir, "images")
    if (!imagesDir.exists()) {
      imagesDir.mkdirs()
    }
    val file = File(imagesDir, "camera_photo_${System.currentTimeMillis()}.jpg")
    val authority = "${context.packageName}.fileprovider"
    val uri = FileProvider.getUriForFile(context, authority, file)
    return Pair(uri, file)
  }

  /**
   * Persists a picked/taken photo into internal app storage so that permissions never expire
   * and the photo remains permanently associated with the member profile.
   */
  fun savePhotoToInternalStorage(context: Context, sourceUri: Uri, memberIdentifier: String): Uri? {
    return try {
      val photosDir = File(context.filesDir, "profile_photos")
      if (!photosDir.exists()) {
        photosDir.mkdirs()
      }

      val cleanId = memberIdentifier.replace(Regex("[^a-zA-Z0-9]"), "_").ifBlank { "member" }
      val targetFile = File(photosDir, "photo_${cleanId}.jpg")

      context.contentResolver.openInputStream(sourceUri)?.use { input ->
        // Decode bitmap and compress nicely to keep ID card performance optimal
        val bitmap = BitmapFactory.decodeStream(input)
        if (bitmap != null) {
          FileOutputStream(targetFile).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
          }
          Uri.fromFile(targetFile)
        } else {
          // Fallback direct byte stream copy
          context.contentResolver.openInputStream(sourceUri)?.use { rawInput ->
            FileOutputStream(targetFile).use { rawOutput ->
              rawInput.copyTo(rawOutput)
            }
          }
          Uri.fromFile(targetFile)
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }
}

/**
 * Bottom sheet allowing member to select from Gallery or take a photo with the Camera.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerModalSheet(
  onDismiss: () -> Unit,
  onPhotoSelected: (Uri) -> Unit,
  onPhotoRemoved: (() -> Unit)? = null,
  hasExistingPhoto: Boolean = false,
  memberIdentifier: String = "new_member"
) {
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
  var previewUri by remember { mutableStateOf<Uri?>(null) }
  var showPreviewDialog by remember { mutableStateOf(false) }

  // Gallery Picker Launcher
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      previewUri = uri
      showPreviewDialog = true
    }
  }

  // Camera TakePicture Launcher
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicture()
  ) { success: Boolean ->
    if (success && pendingCameraUri != null) {
      previewUri = pendingCameraUri
      showPreviewDialog = true
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp, top = 8.dp)
    ) {
      Text(
        text = "Member Profile Photo",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = "Take a photo or choose from gallery. This photo appears on your official digital Member ID Card.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
      )

      // Option 1: Take Photo with Camera
      Surface(
        onClick = {
          val (uri, _) = ProfilePhotoManager.createCameraTempUri(context)
          pendingCameraUri = uri
          cameraLauncher.launch(uri)
        },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("photo_picker_camera_option")
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Take Photo with Camera",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "Use device camera for instant ID photo",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Option 2: Choose from Gallery
      Surface(
        onClick = {
          galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
          )
        },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("photo_picker_gallery_option")
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PhotoLibrary,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(16.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Choose from Gallery",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "Select an existing portrait from your device",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Option 3: Remove Current Photo (if one is already uploaded)
      if (hasExistingPhoto && onPhotoRemoved != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          onClick = {
            scope.launch {
              sheetState.hide()
              onPhotoRemoved()
              onDismiss()
            }
          },
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("photo_picker_remove_option")
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Remove Profile Photo",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.error
              )
              Text(
                text = "Revert to standard Foundation initials emblem",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
              )
            }
          }
        }
      }
    }
  }

  // Preview Dialog before confirming selection
  if (showPreviewDialog && previewUri != null) {
    PhotoPreviewDialog(
      imageUri = previewUri!!,
      onConfirm = {
        val finalUri = ProfilePhotoManager.savePhotoToInternalStorage(
          context = context,
          sourceUri = previewUri!!,
          memberIdentifier = memberIdentifier
        ) ?: previewUri!!
        scope.launch {
          sheetState.hide()
          onPhotoSelected(finalUri)
          showPreviewDialog = false
          onDismiss()
        }
      },
      onRetakeOrChooseOther = {
        showPreviewDialog = false
      },
      onDismiss = {
        showPreviewDialog = false
      }
    )
  }
}

/**
 * Preview dialog allowing the member to verify the photo before saving.
 */
@Composable
fun PhotoPreviewDialog(
  imageUri: Uri,
  onConfirm: () -> Unit,
  onRetakeOrChooseOther: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Preview ID Photo",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Here is how your portrait will appear on your digital Member ID Card:",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        // Photo Frame Preview (matching ID card aspect)
        Box(
          modifier = Modifier
            .size(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF1F5F9))
            .border(2.5.dp, Color(0xFF1B5E5B), RoundedCornerShape(20.dp))
            .testTag("photo_preview_frame"),
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(imageUri)
              .crossfade(true)
              .build(),
            contentDescription = "Selected Photo Preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "Quality: Crisp & centered",
          style = MaterialTheme.typography.labelSmall,
          color = Color(0xFF1B5E5B)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E5B)),
        modifier = Modifier.testTag("photo_preview_confirm_button")
      ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Use Photo")
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onRetakeOrChooseOther,
        modifier = Modifier.testTag("photo_preview_retake_button")
      ) {
        Text("Choose Other")
      }
    }
  )
}
