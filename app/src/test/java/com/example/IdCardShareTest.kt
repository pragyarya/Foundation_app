package com.example

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.model.MemberProfile
import com.example.model.MemberRole
import com.example.ui.components.IdCardExportHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class IdCardShareTest {

  private val testMember = MemberProfile(
    id = "ASP-7749",
    fullName = "Taylor Morgan",
    email = "taylor.morgan@allies.org",
    phone = "+1 555 456 7890",
    role = MemberRole.EXECUTIVE_MEMBER,
    designation = "Executive Member",
    memberSince = "March 2024",
    bloodGroup = "O+",
    emergencyContact = "+1 555 000 1122"
  )

  @Test
  fun testRenderIdCardBitmapProducesValidImage() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val bitmap = IdCardExportHelper.renderIdCardBitmap(context, testMember)

    assertNotNull(bitmap)
    assertEquals(1200, bitmap.width)
    assertEquals(760, bitmap.height)
  }

  @Test
  fun testShareMemberIdLaunchesSendIntent() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    IdCardExportHelper.shareMemberId(context, testMember)

    val shadowApp = shadowOf(context as android.app.Application)
    val nextStartedActivity = shadowApp.nextStartedActivity
    assertNotNull(nextStartedActivity)
    assertEquals(Intent.ACTION_CHOOSER, nextStartedActivity.action)

    val targetIntent = nextStartedActivity.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
    assertNotNull(targetIntent)
    assertEquals(Intent.ACTION_SEND, targetIntent?.action)
    assertEquals("text/plain", targetIntent?.type)
    assertTrue(targetIntent?.getStringExtra(Intent.EXTRA_TEXT)?.contains("Taylor Morgan") == true)
    assertTrue(targetIntent?.getStringExtra(Intent.EXTRA_TEXT)?.contains("ASP-7749") == true)
  }

  @Test
  fun testShareIdCardImageLaunchesSendIntentWithImageStream() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    IdCardExportHelper.shareIdCardImage(context, testMember)

    val shadowApp = shadowOf(context as android.app.Application)
    val nextStartedActivity = shadowApp.nextStartedActivity
    assertNotNull(nextStartedActivity)
    assertEquals(Intent.ACTION_CHOOSER, nextStartedActivity.action)

    val targetIntent = nextStartedActivity.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)
    assertNotNull(targetIntent)
    assertEquals(Intent.ACTION_SEND, targetIntent?.action)
    assertEquals("image/png", targetIntent?.type)
    assertNotNull(targetIntent?.getParcelableExtra(Intent.EXTRA_STREAM))

    // Verify cache file was created
    val cachedImage = File(context.cacheDir, "images/Allies_Foundation_ID_ASP_7749.png")
    assertTrue(cachedImage.exists())
    assertTrue(cachedImage.length() > 0)
  }
}
