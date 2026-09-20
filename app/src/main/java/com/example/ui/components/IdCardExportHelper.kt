package com.example.ui.components

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.MemberProfile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object IdCardExportHelper {

  /**
   * Generates a high-resolution, professional digital ID card bitmap and saves it
   * to the user's device Gallery / Downloads via MediaStore.
   */
  fun saveIdCardToDevice(context: Context, member: MemberProfile): Boolean {
    return try {
      val bitmap = renderIdCardBitmap(context, member)
      val filename = "Allies_Foundation_ID_${member.id.replace(Regex("[^a-zA-Z0-9]"), "_")}.png"

      val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AlliesFoundation")
          put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
      }

      val resolver = context.contentResolver
      val uri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

      if (uri != null) {
        val stream: OutputStream? = resolver.openOutputStream(uri)
        if (stream != null) {
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
          stream.flush()
          stream.close()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          contentValues.clear()
          contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
          resolver.update(uri, contentValues, null, null)
        }

        Toast.makeText(
          context,
          "Member ID Card saved to device Gallery (Pictures/AlliesFoundation)",
          Toast.LENGTH_LONG
        ).show()
        true
      } else {
        Toast.makeText(context, "Failed to create image file.", Toast.LENGTH_SHORT).show()
        false
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Error saving ID card: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
      false
    }
  }

  /**
   * Alias for saveIdCardToDevice for clean naming semantics.
   */
  fun exportIdCardAsImage(context: Context, member: MemberProfile): Boolean {
    return saveIdCardToDevice(context, member)
  }

  /**
   * Shares the Digital Member ID Card directly as an image to other apps
   * using Android's native share intent (ACTION_SEND with image/png MIME and FileProvider).
   */
  fun shareIdCardImage(context: Context, member: MemberProfile) {
    try {
      val bitmap = renderIdCardBitmap(context, member)
      val imagesFolder = File(context.cacheDir, "images")
      if (!imagesFolder.exists()) {
        imagesFolder.mkdirs()
      }

      val sanitizedId = member.id.replace(Regex("[^a-zA-Z0-9]"), "_")
      val imageFile = File(imagesFolder, "Allies_Foundation_ID_${sanitizedId}.png")
      val fos = FileOutputStream(imageFile)
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
      fos.flush()
      fos.close()

      val authority = "${context.packageName}.fileprovider"
      val contentUri: Uri = FileProvider.getUriForFile(context, authority, imageFile)

      val shareText = """
        Official Digital Member ID Card
        Member: ${member.fullName} (${member.id})
        Designation: ${member.designation}
        Status: ${member.validThrough}
        
        Allies for Strays and People Foundation
        Strays • People • Community
      """.trimIndent()

      val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        putExtra(Intent.EXTRA_SUBJECT, "Allies Foundation Member ID Card: ${member.fullName}")
        putExtra(Intent.EXTRA_TEXT, shareText)
        clipData = ClipData.newRawUri("Member ID Card", contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }

      val chooser = Intent.createChooser(shareIntent, "Share Member ID Card")
      chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      if (context !is android.app.Activity) {
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
    } catch (e: Exception) {
      e.printStackTrace()
      Toast.makeText(context, "Error sharing ID card image: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
  }

  /**
   * Shares the Member ID Card credentials as text or formatted card details.
   */
  fun shareMemberId(context: Context, member: MemberProfile) {
    val shareText = """
      Allies for Strays and People Foundation
      OFFICIAL MEMBER IDENTITY CARD
      -----------------------------------------
      Member Name: ${member.fullName}
      Member ID: ${member.id}
      Designation: ${member.designation}
      Status: ${member.validThrough}
      Member Since: ${member.memberSince}
      Registered Email: ${member.email.ifBlank { "Not provided" }}
      Phone: ${member.phone.ifBlank { "Not provided" }}
      ${if (!member.bloodGroup.isNullOrBlank()) "Blood Group: ${member.bloodGroup}\n" else ""}${if (!member.emergencyContact.isNullOrBlank()) "Emergency Contact: ${member.emergencyContact}\n" else ""}-----------------------------------------
      Allies for Strays and People Foundation
      Strays • People • Community
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Member ID Card: ${member.fullName} (${member.id})")
      putExtra(Intent.EXTRA_TEXT, shareText)
    }
    val chooser = Intent.createChooser(intent, "Share Member ID Credentials")
    if (context !is android.app.Activity) {
      chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooser)
  }

  /**
   * Renders a clean, high-resolution 1200x760 (card aspect ratio) card bitmap for saving and sharing.
   */
  fun renderIdCardBitmap(context: Context, member: MemberProfile): Bitmap {
    val width = 1200
    val height = 760
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Card background
    paint.color = android.graphics.Color.rgb(255, 255, 255)
    val cardRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    canvas.drawRoundRect(cardRect, 32f, 32f, paint)

    // Outer subtle card border
    paint.color = android.graphics.Color.rgb(220, 230, 228)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 3f
    canvas.drawRoundRect(cardRect, 32f, 32f, paint)
    paint.style = Paint.Style.FILL

    // Top Header Ribbon (Foundation Teal / Forest)
    paint.color = android.graphics.Color.rgb(27, 94, 91)
    val headerRect = RectF(0f, 0f, width.toFloat(), 150f)
    canvas.drawRect(headerRect, paint)

    // Accent Gold Line below header
    paint.color = android.graphics.Color.rgb(217, 119, 6)
    paint.strokeWidth = 8f
    canvas.drawLine(0f, 150f, width.toFloat(), 150f, paint)


    // Official Foundation Logo in Header Ribbon: Preserves original proportions without cropping or effects
    try {
      val logoBitmap = BitmapFactory.decodeResource(context.resources, com.example.R.drawable.ic_foundation_logo)
      if (logoBitmap != null) {
        val maxLogoW = 90f
        val maxLogoH = 90f
        val logoAspect = logoBitmap.width.toFloat() / logoBitmap.height.toFloat()
        val drawW: Float
        val drawH: Float
        if (logoAspect >= 1f) {
          drawW = maxLogoW
          drawH = maxLogoW / logoAspect
        } else {
          drawH = maxLogoH
          drawW = maxLogoH * logoAspect
        }
        val logoLeft = 45f + (maxLogoW - drawW) / 2f
        val logoTop = 30f + (maxLogoH - drawH) / 2f
        val logoRect = RectF(logoLeft, logoTop, logoLeft + drawW, logoTop + drawH)
        canvas.drawBitmap(logoBitmap, null, logoRect, paint)
      }
    } catch (_: Exception) {
    }

    // Header Text
    paint.color = android.graphics.Color.WHITE
    paint.textSize = 33f
    paint.isFakeBoldText = true
    canvas.drawText("ALLIES FOR STRAYS AND PEOPLE FOUNDATION", 155f, 65f, paint)

    paint.textSize = 22f
    paint.isFakeBoldText = false
    paint.color = android.graphics.Color.rgb(230, 245, 240)
    canvas.drawText("OFFICIAL MEMBER IDENTITY CARD  •  Strays • People • Community", 155f, 108f, paint)

    // Photo Box Placeholder / Frame
    val photoLeft = 60f
    val photoTop = 185f
    val photoSize = 220f
    paint.color = android.graphics.Color.rgb(240, 245, 244)
    val photoRect = RectF(photoLeft, photoTop, photoLeft + photoSize, photoTop + photoSize)
    canvas.drawRoundRect(photoRect, 20f, 20f, paint)

    var hasDrawnPhoto = false
    if (!member.photoUri.isNullOrBlank()) {
      try {
        val imageUri = Uri.parse(member.photoUri)
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalPhoto = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (originalPhoto != null) {
          val scaledPhoto = Bitmap.createScaledBitmap(originalPhoto, photoSize.toInt(), photoSize.toInt(), true)
          val roundedPhoto = Bitmap.createBitmap(photoSize.toInt(), photoSize.toInt(), Bitmap.Config.ARGB_8888)
          val photoCanvas = Canvas(roundedPhoto)
          val photoPaint = Paint(Paint.ANTI_ALIAS_FLAG)
          val rRect = RectF(0f, 0f, photoSize, photoSize)
          photoCanvas.drawRoundRect(rRect, 20f, 20f, photoPaint)
          photoPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
          photoCanvas.drawBitmap(scaledPhoto, 0f, 0f, photoPaint)
          canvas.drawBitmap(roundedPhoto, photoLeft, photoTop, null)
          hasDrawnPhoto = true
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }


    if (!hasDrawnPhoto) {
      // Photo Initials in center of box
      paint.color = android.graphics.Color.rgb(27, 94, 91)
      paint.textSize = 64f
      paint.isFakeBoldText = true
      val initials = member.fullName.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "ASP" }
      canvas.drawText(initials, photoLeft + 55f, photoTop + 130f, paint)
    }

    // Border around photo box
    paint.color = android.graphics.Color.rgb(27, 94, 91)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 4f
    canvas.drawRoundRect(photoRect, 20f, 20f, paint)
    paint.style = Paint.Style.FILL

    // Member Identity Text (Right of photo)
    val infoLeft = 320f
    paint.color = android.graphics.Color.rgb(20, 30, 30)
    paint.textSize = 44f
    paint.isFakeBoldText = true
    canvas.drawText(member.fullName, infoLeft, 235f, paint)

    // Designation Tag
    paint.color = android.graphics.Color.rgb(235, 245, 242)
    val desigRect = RectF(infoLeft, 258f, infoLeft + 420f, 312f)
    canvas.drawRoundRect(desigRect, 12f, 12f, paint)

    paint.color = android.graphics.Color.rgb(27, 94, 91)
    paint.textSize = 25f
    paint.isFakeBoldText = true
    canvas.drawText(member.designation, infoLeft + 20f, 295f, paint)

    // Status: Active Verified
    paint.color = android.graphics.Color.rgb(22, 101, 52)
    paint.textSize = 24f
    paint.isFakeBoldText = true
    canvas.drawText("● ${member.validThrough}", infoLeft + 450f, 295f, paint)

    // Details Grid Row 1
    paint.color = android.graphics.Color.rgb(100, 115, 115)
    paint.textSize = 21f
    paint.isFakeBoldText = false
    canvas.drawText("MEMBER ID", infoLeft, 365f, paint)
    canvas.drawText("MEMBER SINCE", infoLeft + 320f, 365f, paint)

    paint.color = android.graphics.Color.rgb(20, 30, 30)
    paint.textSize = 30f
    paint.isFakeBoldText = true
    canvas.drawText(member.id, infoLeft, 405f, paint)
    canvas.drawText(member.memberSince, infoLeft + 320f, 405f, paint)

    // Contact info Row 2
    paint.color = android.graphics.Color.rgb(100, 115, 115)
    paint.textSize = 21f
    paint.isFakeBoldText = false
    canvas.drawText("REGISTERED EMAIL", infoLeft, 465f, paint)
    canvas.drawText("PHONE / CONTACT", infoLeft + 440f, 465f, paint)

    paint.color = android.graphics.Color.rgb(20, 30, 30)
    paint.textSize = 25f
    paint.isFakeBoldText = true
    val displayEmail = if (member.email.isNotBlank()) member.email else "Registered on file"
    canvas.drawText(displayEmail, infoLeft, 500f, paint)
    val displayPhone = if (member.phone.isNotBlank()) member.phone else "Registered on file"
    canvas.drawText(displayPhone, infoLeft + 440f, 500f, paint)

    // Row 3: Blood Group & Emergency Contact (if provided)
    if (!member.bloodGroup.isNullOrBlank() || !member.emergencyContact.isNullOrBlank()) {
      paint.color = android.graphics.Color.rgb(100, 115, 115)
      paint.textSize = 20f
      paint.isFakeBoldText = false

      if (!member.bloodGroup.isNullOrBlank()) {
        canvas.drawText("BLOOD GROUP: ${member.bloodGroup}", infoLeft, 555f, paint)
      }
      if (!member.emergencyContact.isNullOrBlank()) {
        canvas.drawText("EMERGENCY: ${member.emergencyContact}", infoLeft + 320f, 555f, paint)
      }
    }

    // Bottom Bar (Security & Barcode representation)
    paint.color = android.graphics.Color.rgb(245, 247, 246)
    val footerRect = RectF(0f, 630f, width.toFloat(), height.toFloat())
    canvas.drawRect(footerRect, paint)

    paint.color = android.graphics.Color.rgb(200, 210, 208)
    paint.strokeWidth = 2f
    canvas.drawLine(0f, 630f, width.toFloat(), 630f, paint)

    // Simulated Barcode bars
    paint.color = android.graphics.Color.rgb(40, 50, 50)
    val barcodeStart = 60f
    val barHeights = 50f
    var currX = barcodeStart
    val pattern = listOf(4f, 2f, 6f, 3f, 2f, 5f, 3f, 4f, 2f, 7f, 3f, 2f, 5f, 4f, 2f, 6f, 3f, 2f, 4f, 5f)
    for (bar in pattern) {
      canvas.drawRect(currX, 655f, currX + bar, 655f + barHeights, paint)
      currX += bar + 4f
    }
    paint.textSize = 20f
    paint.isFakeBoldText = false
    canvas.drawText(member.id, currX + 20f, 688f, paint)

    // Signatory
    paint.color = android.graphics.Color.rgb(80, 95, 95)
    paint.textSize = 20f
    paint.isFakeBoldText = true
    canvas.drawText("AUTHORIZED SIGNATORY", 830f, 675f, paint)
    paint.textSize = 18f
    paint.isFakeBoldText = false
    canvas.drawText("Allies for Strays and People Foundation", 830f, 702f, paint)

    return bitmap
  }
}
