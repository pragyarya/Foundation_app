package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.FoundationRepository
import com.example.model.MemberProfile
import com.example.ui.components.DigitalMemberIdCard
import com.example.ui.components.IdCardExportHelper
import com.example.ui.components.PhotoPickerModalSheet
import com.example.ui.components.ShareIdCardBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  onNavigateBack: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentMember by FoundationRepository.currentMember.collectAsState()

  val member = currentMember ?: MemberProfile(
    id = "ASP-0000",
    fullName = "Foundation Member",
    email = "",
    phone = "",
    memberSince = "Current Member"
  )

  var showFullScreenCard by remember { mutableStateOf(false) }
  var showShareSheet by remember { mutableStateOf(false) }
  var showPhotoPickerSheet by remember { mutableStateOf(false) }
  var showEditDialog by remember { mutableStateOf(false) }
  var showLogoutConfirm by remember { mutableStateOf(false) }

  var editPhone by remember(member) { mutableStateOf(member.phone) }
  var editDesignation by remember(member) { mutableStateOf(member.designation) }
  var editAddress by remember(member) { mutableStateOf(member.address.orEmpty()) }
  var editEmergency by remember(member) { mutableStateOf(member.emergencyContact.orEmpty()) }
  var editBloodGroup by remember(member) { mutableStateOf(member.bloodGroup.orEmpty()) }

  val scrollState = rememberScrollState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Digital Member ID Card",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("profile_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back"
            )
          }
        },
        actions = {
          IconButton(
            onClick = { showPhotoPickerSheet = true },
            modifier = Modifier.testTag("profile_camera_button")
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Update ID Photo"
            )
          }
          IconButton(
            onClick = { showEditDialog = true },
            modifier = Modifier.testTag("profile_edit_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Profile"
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.TopCenter
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 600.dp)
          .verticalScroll(scrollState)
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        // 1. ID Card Instructions & Photo Update notice
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Official Foundation Identity Card",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )

          TextButton(
            onClick = { showPhotoPickerSheet = true },
            modifier = Modifier.testTag("profile_change_photo_text_btn")
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Update Photo", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          }
        }

        // 3. The Digital Member ID Card
        DigitalMemberIdCard(
          member = member,
          isFullScreen = false,
          onCardClick = { showFullScreenCard = true },
          onPhotoClick = { showPhotoPickerSheet = true },
          modifier = Modifier.testTag("profile_digital_id_card")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Primary ID Card Action Bar: Share, Save & Full-Screen
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = { showShareSheet = true },
            modifier = Modifier
              .weight(1.2f)
              .height(48.dp)
              .testTag("profile_share_card_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            )
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = "Share ID Card",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Share Card",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }

          OutlinedButton(
            onClick = {
              IdCardExportHelper.saveIdCardToDevice(context, member)
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("profile_download_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Save ID Card",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Save",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }

          OutlinedButton(
            onClick = { showFullScreenCard = true },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("profile_fullscreen_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Fullscreen,
              contentDescription = "View Full Screen",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Expand",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Direct Share Card text option
        OutlinedButton(
          onClick = {
            IdCardExportHelper.shareMemberId(context, member)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("profile_share_text_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Share Card Details as Text",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Basic Member Information Card
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Member Record Information",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )

          TextButton(onClick = { showEditDialog = true }) {
            Text("Edit Info")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            ProfileDetailRow(
              icon = Icons.Default.Email,
              label = "Registered Email",
              value = member.email.ifBlank { "Not provided" }
            )
            ProfileDetailRow(
              icon = Icons.Default.Call,
              label = "Contact Phone",
              value = member.phone.ifBlank { "Not provided" }
            )
            ProfileDetailRow(
              icon = Icons.Default.Home,
              label = "Community Address",
              value = member.address?.ifBlank { "Not provided" } ?: "Not provided"
            )
            ProfileDetailRow(
              icon = Icons.Default.ContactEmergency,
              label = "Emergency Contact",
              value = member.emergencyContact?.ifBlank { "Not provided" } ?: "Not provided"
            )
            ProfileDetailRow(
              icon = Icons.Default.Fingerprint,
              label = "Blood Group",
              value = member.bloodGroup?.ifBlank { "Not provided" } ?: "Not provided"
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Out Button
        OutlinedButton(
          onClick = { showLogoutConfirm = true },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("profile_logout_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
          ),
          border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
          )
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Sign Out from Member Portal",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }

    // Photo Picker Modal Bottom Sheet
    if (showPhotoPickerSheet) {
      PhotoPickerModalSheet(
        onDismiss = { showPhotoPickerSheet = false },
        onPhotoSelected = { uri ->
          FoundationRepository.updateMemberPhoto(uri.toString())
          showPhotoPickerSheet = false
        },
        onPhotoRemoved = if (!member.photoUri.isNullOrBlank()) {
          {
            FoundationRepository.updateMemberPhoto(null)
            showPhotoPickerSheet = false
          }
        } else null,
        hasExistingPhoto = !member.photoUri.isNullOrBlank(),
        memberIdentifier = member.id
      )
    }

    // Full-Screen ID Card View Dialog
    if (showFullScreenCard) {
      Dialog(
        onDismissRequest = { showFullScreenCard = false },
        properties = DialogProperties(usePlatformDefaultWidth = false)
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xEE0F172A))
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .widthIn(max = 520.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Official Digital Identity Card",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )

              TextButton(onClick = { showFullScreenCard = false }) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DigitalMemberIdCard(
              member = member,
              isFullScreen = true,
              onPhotoClick = {
                showFullScreenCard = false
                showPhotoPickerSheet = true
              }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  IdCardExportHelper.shareIdCardImage(context, member)
                },
                modifier = Modifier
                  .weight(1.2f)
                  .height(48.dp)
                  .testTag("profile_fs_share_image_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF1B5E5B),
                  contentColor = Color.White
                )
              ) {
                Icon(Icons.Default.Share, contentDescription = "Share Card Image", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share Image", fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  IdCardExportHelper.saveIdCardToDevice(context, member)
                },
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("profile_fs_save_gallery_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF334155),
                  contentColor = Color.White
                )
              ) {
                Icon(Icons.Default.Download, contentDescription = "Save to Gallery", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save", fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  IdCardExportHelper.shareMemberId(context, member)
                },
                modifier = Modifier
                  .weight(1f)
                  .height(48.dp)
                  .testTag("profile_fs_share_id_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF334155),
                  contentColor = Color.White
                )
              ) {
                Text("Share ID", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Share Options Bottom Sheet
    if (showShareSheet) {
      ShareIdCardBottomSheet(
        member = member,
        onDismiss = { showShareSheet = false },
        onShareImage = {
          IdCardExportHelper.shareIdCardImage(context, member)
        },
        onExportGallery = {
          IdCardExportHelper.saveIdCardToDevice(context, member)
        },
        onShareText = {
          IdCardExportHelper.shareMemberId(context, member)
        }
      )
    }

    // Edit Member Information Dialog
    if (showEditDialog) {
      AlertDialog(
        onDismissRequest = { showEditDialog = false },
        title = {
          Text(
            "Edit Member Information",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        },
        text = {
          Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
              value = editDesignation,
              onValueChange = { editDesignation = it },
              label = { Text("Foundation Designation") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = editPhone,
              onValueChange = { editPhone = it },
              label = { Text("Contact Phone") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = editAddress,
              onValueChange = { editAddress = it },
              label = { Text("Community Address") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = editEmergency,
              onValueChange = { editEmergency = it },
              label = { Text("Emergency Contact") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = editBloodGroup,
              onValueChange = { editBloodGroup = it },
              label = { Text("Blood Group") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              val updated = member.copy(
                designation = editDesignation.trim().ifBlank { member.designation },
                phone = editPhone.trim(),
                address = editAddress.trim().ifBlank { null },
                emergencyContact = editEmergency.trim().ifBlank { null },
                bloodGroup = editBloodGroup.trim().ifBlank { null },
                // Save changes
              )
              FoundationRepository.updateProfile(updated)
              showEditDialog = false
            }
          ) {
            Text("Save Changes")
          }
        },
        dismissButton = {
          TextButton(onClick = { showEditDialog = false }) {
            Text("Cancel")
          }
        }
      )
    }

    // Sign Out Confirmation
    if (showLogoutConfirm) {
      AlertDialog(
        onDismissRequest = { showLogoutConfirm = false },
        title = { Text("Sign Out") },
        text = { Text("Are you sure you want to sign out from the member portal?") },
        confirmButton = {
          Button(
            onClick = {
              showLogoutConfirm = false
              FoundationRepository.logout()
              onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
          ) {
            Text("Sign Out")
          }
        },
        dismissButton = {
          TextButton(onClick = { showLogoutConfirm = false }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}

@Composable
private fun ProfileDetailRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  value: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
      )
    }
    Spacer(modifier = Modifier.width(14.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}
