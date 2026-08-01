package com.homedistill.alcoholcalc.core.calculators

data class PressureResult(
    val boilingTempC: Double,
    val vaporAbvPct: Double,
)

/**
 * Simplified ethanol-water vapor-liquid equilibrium at 760 mmHg, plus a linear
 * boiling-point/pressure correction (0.037 °C per mmHg). Approximate: real VLE curves
 * are not perfectly linear between nodes and the pressure coefficient varies with
 * composition; good enough for home-distillation vacuum/pressure planning.
 */
private val massPctNodes = doubleArrayOf(0.0, 5.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 89.4, 95.0, 100.0)
private val boilTNodes = doubleArrayOf(100.0, 95.5, 91.8, 87.3, 84.7, 83.0, 81.7, 80.7, 79.8, 79.0, 78.15, 78.15, 78.3)
private val vaporPctNodes = doubleArrayOf(0.0, 33.2, 44.2, 53.4, 57.6, 61.4, 65.4, 69.9, 75.3, 81.6, 89.4, 92.0, 100.0)

private const val PRESSURE_COEFFICIENT_C_PER_MMHG = 0.037
private const val REFERENCE_PRESSURE_MMHG = 760.0

/**
 * @param pressureMmHg absolute pressure in the still, mmHg.
 * @param cubeAbvPct volumetric ABV of the liquid in the still. Must be in [0, 100].
 * @return null when [cubeAbvPct] is out of range.
 */
fun calculatePressureCorrection(pressureMmHg: Double, cubeAbvPct: Double): PressureResult? {
    if (cubeAbvPct.isNaN() || cubeAbvPct < 0.0 || cubeAbvPct > 100.0) return null

    val massPctCube = AlcoholDensity.massFractionFromAbv(cubeAbvPct) * 100.0
    val tBase = interpolateLinear(massPctNodes, boilTNodes, massPctCube)
    val yMass = interpolateLinear(massPctNodes, vaporPctNodes, massPctCube)

    val t = tBase + (pressureMmHg - REFERENCE_PRESSURE_MMHG) * PRESSURE_COEFFICIENT_C_PER_MMHG
    val yVol = AlcoholDensity.abvFromMassFraction((yMass / 100.0).coerceIn(0.0, 1.0))

    return PressureResult(t, yVol)
}
