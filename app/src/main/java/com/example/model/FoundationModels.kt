package com.example.model

enum class MemberRole(val displayName: String) {
  VOLUNTEER("Registered Volunteer"),
  FOSTER_PARENT("Foster Caregiver"),
  COMMUNITY_FEEDER("Community Stray Feeder"),
  FOUNDATION_MEMBER("Foundation Member"),
  EXECUTIVE_MEMBER("Executive Member")
}

data class MemberProfile(
  val id: String,
  val fullName: String,
  val email: String,
  val phone: String,
  val role: MemberRole = MemberRole.FOUNDATION_MEMBER,
  val designation: String = role.displayName,
  val memberSince: String,
  val photoUri: String? = null,
  val bloodGroup: String? = null,
  val emergencyContact: String? = null,
  val address: String? = null,
  val validThrough: String = "Active • Verified Member",
  val isDemoAccount: Boolean = false
)
