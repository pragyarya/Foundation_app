package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.FoundationRepository
import com.example.model.MemberProfile

/**
 * High-craft, realistic digital identity card for Allies for Strays and People Foundation members.
 * Includes official branding, member photo/avatar, designation, Member ID, joining date, and
 * security elements.
 */
@Composable
fun DigitalMemberIdCard(
  member: MemberProfile,
  modifier: Modifier = Modifier,
  isFullScreen: Boolean = false,
  onCardClick: (() -> Unit)? = null,
  onPhotoClick: (() -> Unit)? = null
) {
  var showPhotoPickerSheet by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .then(if (onCardClick != null) Modifier.clickable { onCardClick() } else Modifier)
      .testTag("digital_member_id_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isFullScreen) 12.dp else 6.dp),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.linearGradient(
        colors = listOf(
          MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
          MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
        )
      ),
      width = 1.5.dp
    )
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // 1. Top Header Band: Foundation Brand & Title
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color(0xFF1B5E5B), // Deep Foundation Teal
                Color(0xFF247A76),
                Color(0xFF134E4A)
              )
            )
          )
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            // Official Foundation Logo: Preserves original design and proportions without cropping, borders, or effects
            Image(
              painter = painterResource(id = R.drawable.ic_foundation_logo),
              contentDescription = "Allies for Strays and People Foundation Logo",
              modifier = Modifier
                .height(48.dp)
                .widthIn(min = 40.dp, max = 64.dp)
                .testTag("id_card_foundation_logo"),
              contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "ALLIES FOR STRAYS AND PEOPLE",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.5.sp
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "MEMBERSHIP IDENTITY CARD",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = Color(0xFFFDE68A) // Gold accent
              )
              Text(
                text = "Strays • People • Community",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color(0xFFD1FAE5)
              )
            }
          }

          // Verified Member Shield Icon
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White.copy(alpha = 0.15f),
            modifier = Modifier.padding(start = 6.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = "Verified Member",
                tint = Color(0xFFFDE68A),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "OFFICIAL",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp,
                  letterSpacing = 0.8.sp
                ),
                color = Color.White
              )
            }
          }
        }
      }

      // Gold Accent Ribbon
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .background(Color(0xFFD97706))
      )


      // 2. Member Photo & Identity Block
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFFFDFDFD))
          .padding(horizontal = 18.dp, vertical = 14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Member Photo Frame with Photo Picker trigger
          Box(
            modifier = Modifier
              .size(if (isFullScreen) 100.dp else 84.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xFFF1F5F9))
              .border(2.dp, Color(0xFF1B5E5B).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
              .clickable {
                if (onPhotoClick != null) {
                  onPhotoClick()
                } else {
                  showPhotoPickerSheet = true
                }
              }
              .testTag("id_card_photo_frame"),
            contentAlignment = Alignment.Center
          ) {
            val photoModel: Any? = if (!member.photoUri.isNullOrBlank()) {
              member.photoUri
            } else {
              null
            }

            if (photoModel != null) {
              Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                  model = photoModel,
                  contentDescription = "Member Profile Photo",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
                // Small camera icon indicator in the corner
                Box(
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Update Photo",
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                  )
                }
              }
            } else {
              val initials = member.fullName.split(" ")
                .filter { it.isNotBlank() }
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "ASP" }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                  modifier = Modifier
                    .size(if (isFullScreen) 58.dp else 48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B5E5B)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Tap to update photo",
                    tint = Color(0xFF1B5E5B),
                    modifier = Modifier.size(10.dp)
                  )
                  Spacer(modifier = Modifier.width(2.dp))
                  Text(
                    text = "Photo",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color(0xFF1B5E5B)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          // Member Name, Designation & ID
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = member.fullName.ifBlank { "Registered Member" },
              style = if (isFullScreen) MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
              else MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF0F172A),
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.testTag("id_card_member_name")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Designation Badge
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFE6F4F1)
            ) {
              Text(
                text = member.designation.ifBlank { member.role.displayName },
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1B5E5B),
                modifier = Modifier
                  .padding(horizontal = 8.dp, vertical = 3.dp)
                  .testTag("id_card_designation")
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Member ID Display
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "MEMBER ID: ",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF64748B)
              )
              Text(
                text = member.id,
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = FontWeight.ExtraBold,
                  fontFamily = FontFamily.Monospace,
                  letterSpacing = 1.sp
                ),
                color = Color(0xFF0F172A),
                modifier = Modifier.testTag("id_card_member_id")
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Member Attributes Grid (Real information only)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF8FAFC),
          border = CardDefaults.outlinedCardBorder().copy(width = 0.8.dp)
        ) {
          Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              IdCardField(
                label = "JOINING DATE",
                value = member.memberSince,
                icon = Icons.Default.Event,
                modifier = Modifier.weight(1f)
              )
              IdCardField(
                label = "MEMBERSHIP STATUS",
                value = "Active Member",
                icon = Icons.Default.CheckCircle,
                valueColor = Color(0xFF16A34A),
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              IdCardField(
                label = "REGISTERED EMAIL",
                value = member.email.ifBlank { "Not provided" },
                icon = Icons.Default.Email,
                modifier = Modifier.weight(1f)
              )
              IdCardField(
                label = "PHONE NUMBER",
                value = member.phone.ifBlank { "Not provided" },
                icon = Icons.Default.Phone,
                modifier = Modifier.weight(1f)
              )
            }

            if (!member.emergencyContact.isNullOrBlank() || !member.bloodGroup.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                if (!member.bloodGroup.isNullOrBlank()) {
                  IdCardField(
                    label = "BLOOD GROUP",
                    value = member.bloodGroup,
                    icon = Icons.Default.Fingerprint,
                    modifier = Modifier.weight(1f)
                  )
                }
                if (!member.emergencyContact.isNullOrBlank()) {
                  IdCardField(
                    label = "EMERGENCY CONTACT",
                    value = member.emergencyContact,
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f)
                  )
                }
              }
            }
          }
        }
      }

      // 4. Bottom Security & Registry Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0F172A))
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Barcode representation
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.QrCode2,
              contentDescription = "Card Security Code",
              tint = Color(0xFF94A3B8),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "ID CODE: ${member.id}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = Color(0xFFE2E8F0)
              )
              Text(
                text = "Non-transferable • Allies for Strays and People Foundation",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = Color(0xFF64748B)
              )
            }
          }

          Text(
            text = "VERIFIED",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              fontSize = 9.sp
            ),
            color = Color(0xFF34D399)
          )
        }
      }
    }
  }

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
}

@Composable
private fun IdCardField(
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier,
  valueColor: Color = Color(0xFF1E293B)
) {
  Column(modifier = modifier.padding(horizontal = 4.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color(0xFF64748B),
        modifier = Modifier.size(11.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        ),
        color = Color(0xFF64748B)
      )
    }
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
      color = valueColor,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
