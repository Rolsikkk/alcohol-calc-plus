package com.homedistill.alcoholcalc.core.calculators

/** Parses user-entered decimal text, accepting both ',' and '.' as the fractional separator. */
fun parseDecimalInput(input: String): Double? {
    val normalized = input.trim().replace(',', '.')
    if (normalized.isEmpty()) return null
    return normalized.toDoubleOrNull()
}
