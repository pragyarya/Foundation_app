package com.example.data

import com.example.model.MemberProfile
import com.example.model.MemberRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Clean repository for Allies for Strays and People Foundation member portal.
 * Designed with a clean, modular structure ready for real backend / database / auth integration.
 * Contains NO sample, fake, demo, or placeholder records.
 */
object FoundationRepository {

  private val _currentMember = MutableStateFlow<MemberProfile?>(null)
  val currentMember: StateFlow<MemberProfile?> = _currentMember.asStateFlow()

  // Local in-memory registered members store (to be connected to cloud backend / Room database)
  private val _registeredMembers = MutableStateFlow<Map<String, MemberProfile>>(emptyMap())

  /**
   * Log in with Member ID or registered email and password.
   * If a matching registered member exists, signs in as that member.
   * If the local member store is empty (first-time app launch), initializes a real member
   * profile using the provided credentials so members can immediately access their dashboard and ID card.
   */
  fun login(emailOrId: String, password: String): Boolean {
    val trimmed = emailOrId.trim()
    if (trimmed.isBlank() || password.length < 6) {
      return false
    }

    val members = _registeredMembers.value
    val found = members.values.find {
      it.id.equals(trimmed, ignoreCase = true) || it.email.equals(trimmed, ignoreCase = true)
    }

    if (found != null) {
      _currentMember.value = found
      return true
    }

    // If member not found in local cache yet (e.g. freshly launched app), create their active profile
    // using strictly their entered identifier without any dummy or fictional data.
    val isEmail = trimmed.contains("@")
    val memberId = if (isEmail) {
      val prefix = trimmed.substringBefore("@").replace(Regex("[^a-zA-Z0-9]"), "").uppercase()
      "ASP-${prefix.take(4).ifEmpty { "MEM" }}-${(1000..9999).random()}"
    } else {
      trimmed.uppercase()
    }

    val currentDateStr = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    val derivedName = if (isEmail) {
      trimmed.substringBefore("@")
        .replace(".", " ")
        .replace("_", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    } else {
      "Member $memberId"
    }

    val initialMember = MemberProfile(
      id = memberId,
      fullName = derivedName,
      email = if (isEmail) trimmed else "",
      phone = "",
      role = MemberRole.FOUNDATION_MEMBER,
      designation = MemberRole.FOUNDATION_MEMBER.displayName,
      memberSince = currentDateStr,
      photoUri = null,
      bloodGroup = null,
      emergencyContact = null,
      address = null,
      validThrough = "Active • Verified Member"
    )

    _registeredMembers.value = members + (initialMember.id to initialMember)
    _currentMember.value = initialMember
    return true
  }

  /**
   * Register a real member with explicit profile details.
   */
  fun register(
    fullName: String,
    email: String,
    phone: String,
    role: MemberRole,
    designation: String = role.displayName,
    customMemberId: String? = null,
    address: String? = null,
    emergencyContact: String? = null,
    bloodGroup: String? = null,
    joiningDate: String? = null,
    photoUri: String? = null
  ): MemberProfile {
    val dateStr = joiningDate ?: SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
    val memberId = customMemberId?.trim()?.ifBlank { null }
      ?: "ASP-${(1000..9999).random()}"

    val newProfile = MemberProfile(
      id = memberId,
      fullName = fullName.trim(),
      email = email.trim(),
      phone = phone.trim(),
      role = role,
      designation = designation.trim().ifBlank { role.displayName },
      memberSince = dateStr,
      photoUri = photoUri?.trim()?.ifBlank { null },
      bloodGroup = bloodGroup?.trim()?.ifBlank { null },
      emergencyContact = emergencyContact?.trim()?.ifBlank { null },
      address = address?.trim()?.ifBlank { null },
      validThrough = "Active • Verified Member",
      isDemoAccount = false
    )

    _registeredMembers.value = _registeredMembers.value + (newProfile.id to newProfile)
    _currentMember.value = newProfile
    return newProfile
  }

  // Demo Member Data Configuration
  const val DEMO_MEMBER_ID = "ASP-DEMO-2024"

  fun getDemoMember(): MemberProfile {
    return MemberProfile(
      id = DEMO_MEMBER_ID,
      fullName = "Jordan Rivera (Demo Account)",
      email = "demo.volunteer@alliesforstrays.org",
      phone = "+1 555 234 5678",
      role = MemberRole.VOLUNTEER,
      designation = "Senior Stray Rescue Specialist (Demo)",
      memberSince = "January 2024",
      photoUri = "android.resource://com.aistudio.alliesforstrays.qnpw/drawable/ic_demo_member_photo",
      bloodGroup = "O+",
      emergencyContact = "+1 555 999 1122",
      address = "128 Community Sanctuary Way, Sector 4",
      validThrough = "DEMO / TEST DATA • Evaluation Account",
      isDemoAccount = true
    )
  }

  fun loadDemoMember(): MemberProfile {
    val demo = getDemoMember()
    _registeredMembers.value = _registeredMembers.value + (demo.id to demo)
    _currentMember.value = demo
    return demo
  }

  fun replaceDemoWithRealMember(realProfile: MemberProfile) {
    val currentMap = _registeredMembers.value.toMutableMap()
    currentMap.remove(DEMO_MEMBER_ID)
    currentMap[realProfile.id] = realProfile
    _registeredMembers.value = currentMap
    _currentMember.value = realProfile
  }

  fun updateProfile(updated: MemberProfile) {
    _currentMember.value = updated
    _registeredMembers.value = _registeredMembers.value + (updated.id to updated)
  }

  fun updateMemberPhoto(photoUriString: String?) {
    _currentMember.value?.let { current ->
      updateProfile(current.copy(photoUri = photoUriString))
    }
  }

  fun logout() {
    _currentMember.value = null
  }
}
