package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FoundationRepository
import com.example.model.MemberRole
import com.example.ui.components.FoundationLogo
import com.example.ui.components.PhotoPickerModalSheet

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegistrationScreen(
  onRegistrationSuccess: () -> Unit,
  onNavigateToLogin: () -> Unit,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var fullName by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var customMemberId by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var emergencyContact by remember { mutableStateOf("") }
  var bloodGroup by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf(MemberRole.FOUNDATION_MEMBER) }
  var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
  var showPhotoPickerSheet by remember { mutableStateOf(false) }
  var agreedToTerms by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showSuccessDialog by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  fun handleRegister() {
    if (fullName.isBlank()) {
      errorMessage = "Please enter your full legal name."
      return
    }
    if (email.isBlank() || !email.contains("@")) {
      errorMessage = "Please enter a valid registered email address."
      return
    }
    if (phone.isBlank()) {
      errorMessage = "Please enter your contact phone number."
      return
    }
    if (password.length < 6) {
      errorMessage = "Password must be at least 6 characters long."
      return
    }
    if (!agreedToTerms) {
      errorMessage = "Please confirm adherence to Foundation member bylaws."
      return
    }

    errorMessage = null
    FoundationRepository.register(
      fullName = fullName.trim(),
      email = email.trim(),
      phone = phone.trim(),
      role = selectedRole,
      designation = selectedRole.displayName,
      customMemberId = customMemberId.ifBlank { null },
      address = address.ifBlank { null },
      emergencyContact = emergencyContact.ifBlank { null },
      bloodGroup = bloodGroup.ifBlank { null },
      photoUri = selectedPhotoUri?.toString()
    )

    showSuccessDialog = true
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            "Member Registration",
            style = MaterialTheme.typography.titleMedium
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("registration_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back"
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
          .widthIn(max = 550.dp)
          .verticalScroll(scrollState)
          .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        FoundationLogo(size = 72.dp)

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "New Member Registration",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Allies for Strays and People Foundation",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Badge,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "This registration portal is exclusively for new Foundation Members. Volunteer onboarding is administered directly through Foundation registry headquarters.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            // Section 1: Member Profile Photo Upload & Preview
            Text(
              text = "1. Member Profile Photo (for ID Card)",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Photo Preview Box (matches ID card aspect ratio)
                Box(
                  modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F5F9))
                    .border(2.dp, Color(0xFF1B5E5B), RoundedCornerShape(16.dp))
                    .clickable { showPhotoPickerSheet = true }
                    .testTag("reg_photo_preview_box"),
                  contentAlignment = Alignment.Center
                ) {
                  if (selectedPhotoUri != null) {
                    AsyncImage(
                      model = selectedPhotoUri,
                      contentDescription = "Uploaded Member Photo Preview",
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxSize()
                    )
                  } else {
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Select Photo",
                        tint = Color(0xFF1B5E5B),
                        modifier = Modifier.size(28.dp)
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = "Add Photo",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF1B5E5B)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                  if (selectedPhotoUri != null) {
                    Text(
                      text = "✓ Photo Preview Ready",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      color = Color(0xFF166534)
                    )
                    Text(
                      text = "This photo will appear on your official digital Member ID Card.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      OutlinedButton(
                        onClick = { showPhotoPickerSheet = true },
                        modifier = Modifier
                          .height(34.dp)
                          .testTag("reg_change_photo_btn"),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                      ) {
                        Text("Change", style = MaterialTheme.typography.labelSmall)
                      }
                      TextButton(
                        onClick = { selectedPhotoUri = null },
                        modifier = Modifier
                          .height(34.dp)
                          .testTag("reg_remove_photo_btn"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                      ) {
                        Text(
                          "Remove",
                          color = MaterialTheme.colorScheme.error,
                          style = MaterialTheme.typography.labelSmall
                        )
                      }
                    }
                  } else {
                    Text(
                      text = "Take or Select Member Photo",
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Use camera or gallery. A preview will be shown before saving.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                      onClick = { showPhotoPickerSheet = true },
                      modifier = Modifier
                        .height(36.dp)
                        .testTag("reg_select_photo_btn"),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E5B))
                    ) {
                      Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        "Take / Select Photo",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Membership Classification
            Text(
              text = "2. Membership Classification",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              MemberRole.entries.filter { it != MemberRole.VOLUNTEER }.forEach { role ->
                FilterChip(
                  selected = selectedRole == role,
                  onClick = { selectedRole = role },
                  label = { Text(role.displayName, style = MaterialTheme.typography.bodySmall) },
                  leadingIcon = if (selectedRole == role) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                  } else null,
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Real Member Information
            Text(
              text = "3. Real Member Information",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = fullName,
              onValueChange = { fullName = it },
              label = { Text("Full Legal Name *") },
              placeholder = { Text("e.g., Alex Johnson") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_fullname_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Registered Email Address *") },
              placeholder = { Text("alex@example.org") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_email_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Contact Phone Number *") },
              placeholder = { Text("+1 555 019 2834") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_phone_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = customMemberId,
              onValueChange = { customMemberId = it },
              label = { Text("Assigned Member ID (Optional)") },
              placeholder = { Text("Leave blank to auto-generate ASP-XXXX") },
              leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_member_id_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = address,
              onValueChange = { address = it },
              label = { Text("Community Address (Optional)") },
              placeholder = { Text("City, Area / District") },
              leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
              OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Emergency Contact") },
                placeholder = { Text("+1 555 999 0000") },
                singleLine = true,
                modifier = Modifier.weight(1.3f),
                shape = RoundedCornerShape(12.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              OutlinedTextField(
                value = bloodGroup,
                onValueChange = { bloodGroup = it },
                label = { Text("Blood Group") },
                placeholder = { Text("O+") },
                leadingIcon = { Icon(Icons.Default.Fingerprint, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 4: Security Credentials
            Text(
              text = "4. Account Security",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = password,
              onValueChange = { password = it },
              label = { Text("Set Portal Password *") },
              placeholder = { Text("Min. 6 characters") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              visualTransformation = PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_password_input"),
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bylaws confirmation checkbox
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .fillMaxWidth()
                .clickable { agreedToTerms = !agreedToTerms }
            ) {
              Checkbox(
                checked = agreedToTerms,
                onCheckedChange = { agreedToTerms = it },
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("reg_terms_checkbox")
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "I certify that these details are accurate and agree to follow Foundation community guidelines for stray animal rescue & care.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            if (errorMessage != null) {
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = errorMessage.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
              onClick = { handleRegister() },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("reg_submit_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
              )
            ) {
              Text(
                text = "Register Member Profile",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.padding(bottom = 24.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Already have your member credentials?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Sign In",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
              .clickable { onNavigateToLogin() }
              .testTag("reg_to_login_link")
          )
        }
      }
    }

    // Photo Picker Modal Sheet
    if (showPhotoPickerSheet) {
      PhotoPickerModalSheet(
        onDismiss = { showPhotoPickerSheet = false },
        onPhotoSelected = { uri ->
          selectedPhotoUri = uri
          showPhotoPickerSheet = false
        },
        onPhotoRemoved = if (selectedPhotoUri != null) {
          {
            selectedPhotoUri = null
            showPhotoPickerSheet = false
          }
        } else null,
        hasExistingPhoto = selectedPhotoUri != null,
        memberIdentifier = customMemberId.ifBlank { "new_member" }
      )
    }

    if (showSuccessDialog) {
      AlertDialog(
        onDismissRequest = {
          showSuccessDialog = false
          onRegistrationSuccess()
        },
        title = { Text("Registration Complete") },
        text = {
          Text(
            "Your official membership record has been registered" +
              (if (selectedPhotoUri != null) " with your verified ID photo" else "") +
              ". You can now access your Member Dashboard and Digital Identity Card."
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showSuccessDialog = false
              onRegistrationSuccess()
            }
          ) {
            Text("Open Dashboard")
          }
        }
      )
    }
  }
}
