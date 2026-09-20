package com.example

import com.example.data.FoundationRepository
import com.example.model.MemberProfile
import com.example.model.MemberRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FoundationRepositoryTest {

  @Before
  fun setUp() {
    FoundationRepository.logout()
  }

  @Test
  fun testInitialStateHasNoDummyMember() {
    assertNull(FoundationRepository.currentMember.value)
  }

  @Test
  fun testLoginValidation() {
    // Empty inputs should fail
    assertFalse(FoundationRepository.login("", ""))
    assertFalse(FoundationRepository.login("member@example.com", "123")) // password too short
  }

  @Test
  fun testLoginWithRealIdentifier() {
    val success = FoundationRepository.login("ASP-1042", "securePass123")
    assertTrue(success)

    val member = FoundationRepository.currentMember.value
    assertNotNull(member)
    assertEquals("ASP-1042", member?.id)
    assertEquals(MemberRole.FOUNDATION_MEMBER, member?.role)
  }

  @Test
  fun testRegisterRealMember() {
    val registered = FoundationRepository.register(
      fullName = "Jordan Reed",
      email = "jordan.reed@foundation.org",
      phone = "+1 (555) 234-5678",
      role = MemberRole.COMMUNITY_FEEDER,
      designation = "Community Stray Feeder",
      customMemberId = "ASP-2024-9912",
      address = "North Community Sector 4",
      emergencyContact = "+1 (555) 987-6543",
      bloodGroup = "B+"
    )

    assertEquals("ASP-2024-9912", registered.id)
    assertEquals("Jordan Reed", registered.fullName)
    assertEquals("jordan.reed@foundation.org", registered.email)
    assertEquals(MemberRole.COMMUNITY_FEEDER, registered.role)
    assertEquals("B+", registered.bloodGroup)

    val current = FoundationRepository.currentMember.value
    assertNotNull(current)
    assertEquals("ASP-2024-9912", current?.id)
  }

  @Test
  fun testUpdateProfileAndLogout() {
    FoundationRepository.register(
      fullName = "Alex Chen",
      email = "alex.chen@example.org",
      phone = "+1 (555) 345-6789",
      role = MemberRole.VOLUNTEER
    )

    val current = FoundationRepository.currentMember.value
    assertNotNull(current)

    val updated = current!!.copy(
      phone = "+1 (555) 999-0000",
      emergencyContact = "Elena Chen (+1 555 111-2222)"
    )
    FoundationRepository.updateProfile(updated)

    assertEquals("+1 (555) 999-0000", FoundationRepository.currentMember.value?.phone)
    assertEquals("Elena Chen (+1 555 111-2222)", FoundationRepository.currentMember.value?.emergencyContact)

    FoundationRepository.logout()
    assertNull(FoundationRepository.currentMember.value)
  }
}
