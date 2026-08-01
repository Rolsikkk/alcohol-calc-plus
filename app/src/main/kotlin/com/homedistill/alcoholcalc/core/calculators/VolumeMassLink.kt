package com.homedistill.alcoholcalc.core.calculators

/**
 * Converts between a solution's volume and mass at a fixed ABV%, used to keep the
 * mL/g/% triplet on a calculator row mutually consistent when any one of the three
 * fields is edited directly (the other two are re-derived, ABV% held fixed unless
 * ABV% itself is the field being edited).
 */
fun massFromVolume(volumeMl: Double, abvPct: Double): Double =
    volumeMl * AlcoholDensity.densityFromAbv(abvPct)

fun volumeFromMass(massG: Double, abvPct: Double): Double {
    val density = AlcoholDensity.densityFromAbv(abvPct)
    if (density <= 0.0) return 0.0
    return massG / density
}
