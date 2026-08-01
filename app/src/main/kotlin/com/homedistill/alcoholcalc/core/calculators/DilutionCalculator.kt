package com.homedistill.alcoholcalc.core.calculators

sealed class DilutionResult {
    data class Success(
        /** Volume of pure ethanol (AC) contained in the starting spirit, mL. */
        val absoluteAlcoholMl: Double,
        /** Mass of the starting spirit charge, g. */
        val startMassG: Double,
        val waterVolumeMl: Double,
        val waterMassG: Double,
        val resultVolumeMl: Double,
        val resultMassG: Double,
        val resultAbvPct: Double,
    ) : DilutionResult()

    /** Input is physically or logically invalid (non-positive values, or target >= start ABV). */
    object Invalid : DilutionResult()
}

/**
 * Dilutes [v1] mL of spirit at [p1]% ABV down to [target]% ABV by adding water.
 */
fun calculateDilution(v1: Double, p1: Double, target: Double): DilutionResult {
    if (v1 <= 0.0 || p1 <= 0.0 || target <= 0.0 || target >= p1) {
        return DilutionResult.Invalid
    }

    val g1 = v1 * AlcoholDensity.densityFromAbv(p1)
    val massEth = v1 * (p1 / 100.0) * AlcoholDensity.ETHANOL_DENSITY_20C
    val wTarget = AlcoholDensity.massFractionFromAbv(target)
    if (wTarget <= 0.0) return DilutionResult.Invalid

    val totalMass = massEth / wTarget
    val waterMass = maxOf(totalMass - g1, 0.0)
    val waterVol = waterMass / AlcoholDensity.WATER_DENSITY_20C
    val resultDensity = AlcoholDensity.densityFromAbv(target)
    val resultVol = totalMass / resultDensity
    val absoluteAlcoholMl = v1 * p1 / 100.0

    return DilutionResult.Success(
        absoluteAlcoholMl = absoluteAlcoholMl,
        startMassG = g1,
        waterVolumeMl = waterVol,
        waterMassG = waterMass,
        resultVolumeMl = resultVol,
        resultMassG = totalMass,
        resultAbvPct = target,
    )
}
