package com.homedistill.alcoholcalc.core.calculators

data class RectificationResult(
    val absoluteAlcoholMl: Double,
    val headsMl: Double,
    val bodyMl: Double,
    val tailsMl: Double,
    val rawMassG: Double,
)

const val DEFAULT_HEADS_PCT = 8.0
const val DEFAULT_BODY_PCT = 60.0
const val DEFAULT_TAILS_PCT = 30.0

/**
 * Splits a raw spirit charge into heads/body/tails cuts, expressed as volumes of
 * absolute alcohol (AC), based on percentage shares of the total AC.
 */
fun calculateRectification(
    v: Double,
    p: Double,
    headsPct: Double = DEFAULT_HEADS_PCT,
    bodyPct: Double = DEFAULT_BODY_PCT,
    tailsPct: Double = DEFAULT_TAILS_PCT,
): RectificationResult? {
    if (v <= 0.0 || p <= 0.0 || p > 100.0) return null

    val ac = v * p / 100.0
    val heads = ac * headsPct / 100.0
    val body = ac * bodyPct / 100.0
    val tails = ac * tailsPct / 100.0
    val rawMass = v * AlcoholDensity.densityFromAbv(p)

    return RectificationResult(ac, heads, body, tails, rawMass)
}
