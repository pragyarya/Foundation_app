package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

// =========================================================================
// OFFICIAL FOUNDATION LOGO COMPONENT
// =========================================================================
// Preserves the logo's original design, aspect ratio, and proportions.
// No circular cropping, recoloring, effects, or artificial distortion.
// Scales cleanly and responsively across all Android screen sizes using ContentScale.Fit.
// =========================================================================

@Composable
fun FoundationLogo(
  modifier: Modifier = Modifier,
  size: Dp = 104.dp,
  contentDescription: String = "Allies for Strays and People Foundation Logo"
) {
  Box(
    modifier = modifier
      .size(size)
      .testTag("foundation_logo"),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_foundation_logo),
      contentDescription = contentDescription,
      modifier = Modifier.size(size),
      contentScale = ContentScale.Fit
    )
  }
}

/**
 * Compact horizontal header logo badge displaying the exact official Foundation Logo
 * with original proportions intact (no placeholder icons or cropping).
 */
@Composable
fun FoundationLogoBadge(
  modifier: Modifier = Modifier,
  size: Dp = 40.dp,
  contentDescription: String = "Allies for Strays and People Foundation Logo"
) {
  Box(
    modifier = modifier
      .size(size)
      .testTag("foundation_logo_badge"),
    contentAlignment = Alignment.Center
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_foundation_logo),
      contentDescription = contentDescription,
      modifier = Modifier.size(size),
      contentScale = ContentScale.Fit
    )
  }
}
