package com.example.model

enum class MemberRole(val displayName: String) {
    FOUNDATION_MEMBER("Foundation Member"),
    EXECUTIVE_MEMBER("Executive Member"),
    VOLUNTEER("Registered Volunteer")
}

data class MemberProfile(
    val id: String,
    val memberNo: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val photoUri: String? = null,
    val designation: String,
    val department: String,
    val joiningDate: String? = null,
    val status: String = "pending",
    val isActive: Boolean = true
)
