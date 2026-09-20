package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactEmergency
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.FoundationRepository
import com.example.model.MemberProfile
import com.example.ui.components.DigitalMemberIdCard
import com.example.ui.components.IdCardExportHelper
import com.example.ui.components.ShareIdCardBottomSheet

@Composable
fun DashboardScreen(
  onNavigateToProfile: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentMember by FoundationRepository.currentMember.collectAsState()

  var showFullScreenCard by remember { mutableStateOf(false) }
  var showShareSheet by remember { mutableStateOf(false) }
  var showEditDialog by remember { mutableStateOf(false) }
  var showLogoutConfirm by remember { mutableStateOf(false) }

  // Fallback if not logged in (should not normally occur)
  val member = currentMember ?: MemberProfile(
    id = "ASP-0000",
    fullName = "Foundation Member",
    email = "",
    phone = "",
    memberSince = "Current Member"
  )

  var editPhone by remember(member) { mutableStateOf(member.phone) }
  var editAddress by remember(member) { mutableStateOf(member.address.orEmpty()) }
  var editEmergency by remember(member) { mutableStateOf(member.emergencyContact.orEmpty()) }
  var editBloodGroup by remember(member) { mutableStateOf(member.bloodGroup.orEmpty()) }

  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.TopCenter
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 600.dp)
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
      // 1. Top Header Welcome Banner
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  Color(0xFF1B5E5B),
                  Color(0xFF247A76)
                )
              )
            )
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Official Foundation Logo: Preserves original proportions without cropping or effects
              Image(
                painter = painterResource(id = R.drawable.ic_foundation_logo),
                contentDescription = "Allies for Strays and People Foundation Logo",
                modifier = Modifier
                  .height(50.dp)
                  .widthIn(min = 40.dp, max = 64.dp)
                  .testTag("dashboard_foundation_logo"),
                contentScale = ContentScale.Fit
              )

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = "Member Portal",
                  style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = Color(0xFFFDE68A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = member.fullName,
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = Color.White,
                  modifier = Modifier.testTag("dashboard_member_name")
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color.White.copy(alpha = 0.2f),
              modifier = Modifier.clickable { onNavigateToProfile() }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Badge,
                  contentDescription = "ID Card",
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "ID Card",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Designation Tag
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFE6F4F1)
            ) {
              Text(
                text = member.designation,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1B5E5B),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            // Member ID
            Text(
              text = "ID: ${member.id}",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              color = Color.White
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Verified,
              contentDescription = null,
              tint = Color(0xFF34D399),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Allies for Strays and People Foundation • Active Member",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFFD1FAE5)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 2. Digital Identity Card Section (Prominent preview & instant actions)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Official Digital ID Card",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )

        TextButton(onClick = onNavigateToProfile) {
          Text("Full Profile", fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Digital ID Card Preview
      DigitalMemberIdCard(
        member = member,
        isFullScreen = false,
        onCardClick = { showFullScreenCard = true }
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Card Action Buttons: Share, Save & Full Screen
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { showShareSheet = true },
          modifier = Modifier
            .weight(1.2f)
            .height(46.dp)
            .testTag("dashboard_share_card_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          )
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share ID Card",
            modifier = Modifier.size(17.dp)
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
            .height(46.dp)
            .testTag("dashboard_download_card_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Save ID Card",
            modifier = Modifier.size(17.dp)
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
            .height(46.dp)
            .testTag("dashboard_fullscreen_card_button"),
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

      Spacer(modifier = Modifier.height(24.dp))

      // 3. Member Information (Real Data Only)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Member Registry Record",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )

        IconButton(onClick = { showEditDialog = true }) {
          Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit information",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          DashboardInfoItem(
            icon = Icons.Default.Person,
            label = "FULL LEGAL NAME",
            value = member.fullName
          )
          DashboardInfoItem(
            icon = Icons.Default.Badge,
            label = "MEMBER ID",
            value = member.id
          )
          DashboardInfoItem(
            icon = Icons.Default.Event,
            label = "DATE OF JOINING",
            value = member.memberSince
          )
          DashboardInfoItem(
            icon = Icons.Default.Email,
            label = "REGISTERED EMAIL",
            value = member.email.ifBlank { "Not provided" }
          )
          DashboardInfoItem(
            icon = Icons.Default.Call,
            label = "CONTACT PHONE",
            value = member.phone.ifBlank { "Not provided" }
          )
          if (!member.address.isNullOrBlank()) {
            DashboardInfoItem(
              icon = Icons.Default.Home,
              label = "COMMUNITY / ADDRESS",
              value = member.address
            )
          }
          if (!member.emergencyContact.isNullOrBlank()) {
            DashboardInfoItem(
              icon = Icons.Default.ContactEmergency,
              label = "EMERGENCY CONTACT",
              value = member.emergencyContact
            )
          }
          if (!member.bloodGroup.isNullOrBlank()) {
            DashboardInfoItem(
              icon = Icons.Default.Fingerprint,
              label = "BLOOD GROUP",
              value = member.bloodGroup
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 4. Member Activity & Records (Clean Empty State per User Requirements)
      Text(
        text = "Activity & Field Records",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )

      Spacer(modifier = Modifier.height(8.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
      ) {
        Row(
          modifier = Modifier.padding(20.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "No Activity Records Available",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Your official foundation member assignments, records, and updates will appear here once connected to Foundation registry headquarters.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // 5. Account & Session Actions
      OutlinedButton(
        onClick = { showLogoutConfirm = true },
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("dashboard_logout_button"),
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
          modifier = Modifier.size(18.dp)
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

  // Full-Screen ID Card Dialog
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
              text = "Official Member ID",
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
            isFullScreen = true
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
                .testTag("fullscreen_share_image_button"),
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
                .testTag("fullscreen_save_gallery_button"),
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
                .testTag("fullscreen_share_text_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF475569),
                contentColor = Color.White
              )
            ) {
              Text("Details", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Share Card Bottom Sheet
  if (showShareSheet) {
    ShareIdCardBottomSheet(
      member = member,
      onDismiss = { showShareSheet = false },
      onShareImage = { IdCardExportHelper.shareIdCardImage(context, member) },
      onExportGallery = { IdCardExportHelper.saveIdCardToDevice(context, member) },
      onShareText = { IdCardExportHelper.shareMemberId(context, member) }
    )
  }

  // Edit Profile Dialog
  if (showEditDialog) {
    AlertDialog(
      onDismissRequest = { showEditDialog = false },
      title = { Text("Update Member Information") },
      text = {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
          OutlinedTextField(
            value = editPhone,
            onValueChange = { editPhone = it },
            label = { Text("Phone Number") },
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
            FoundationRepository.updateProfile(
              member.copy(
                phone = editPhone.trim(),
                address = editAddress.trim().ifBlank { null },
                emergencyContact = editEmergency.trim().ifBlank { null },
                bloodGroup = editBloodGroup.trim().ifBlank { null }
              )
            )
            showEditDialog = false
          }
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Logout Confirmation
  if (showLogoutConfirm) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirm = false },
      title = { Text("Sign Out") },
      text = { Text("Are you sure you want to sign out of the member portal?") },
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

@Composable
private fun DashboardInfoItem(
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
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 0.5.sp),
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
