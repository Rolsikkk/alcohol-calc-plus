package com.homedistill.alcoholcalc.core.calculators

import java.util.Locale

/** Formats a finite number for display, always using '.' as the decimal separator. */
fun formatDecimal(value: Double, decimals: Int = 2): String {
    if (value.isNaN() || value.isInfinite()) return "0"
    return String.format(Locale.US, "%.${decimals}f", value)
}

/** Nudges a text-field's numeric value by [delta], clamped to [min]/[max]; used by +/- steppers. */
fun stepDecimalText(text: String, delta: Double, min: Double = 0.0, max: Double = 100.0): String {
    val current = parseDecimalInput(text) ?: 0.0
    val next = (current + delta).coerceIn(min, max)
    return formatDecimal(next, 1)
}
