package com.homedistill.alcoholcalc.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Fixed (theme-independent) colors used to color-code calculator fields by physical
 * quantity, matching the reference app's look: volumes in red, percentages in blue,
 * temperatures in green, power in purple, and secondary/computed values in muted gray.
 */
object AppFieldColors {
    val Volume = Color(0xFFB71C1C)
    val Percent = Color(0xFF1565C0)
    val Temperature = Color(0xFF2E7D32)
    val Power = Color(0xFF6A1B9A)
    val Neutral = Color(0xFF212121)
    val Muted = Color(0xFF757575)
    val HighlightBackground = Color(0xFFFFF59D)

    val TopBarBackground = Color(0xFF1A1A1A)
    val TopBarContent = Color(0xFFFFFFFF)
}
