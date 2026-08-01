package com.homedistill.alcoholcalc.core.calculators

/**
 * Density and mass-fraction relationships for ethanol-water mixtures at 20°C.
 * Node table is the domain-provided reference; not the full OIML table.
 */
object AlcoholDensity {

    const val ETHANOL_DENSITY_20C = 0.78934
    const val WATER_DENSITY_20C = 0.99823

    private val abvNodes = doubleArrayOf(
        0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0,
        55.0, 60.0, 65.0, 70.0, 75.0, 80.0, 85.0, 90.0, 95.0, 96.0, 97.0, 98.0, 99.0, 100.0
    )

    private val densityNodes = doubleArrayOf(
        0.99823, 0.99048, 0.98187, 0.97297, 0.96864, 0.95820, 0.95382, 0.94459, 0.93518, 0.92457, 0.91384,
        0.90184, 0.89113, 0.87923, 0.86634, 0.85327, 0.83933, 0.82409, 0.81797, 0.80503, 0.80207, 0.79906, 0.79597, 0.79284, 0.78934
    )

    /** Linear interpolation of density (g/mL) by volumetric ABV%, clamped to [0, 100]. */
    fun densityFromAbv(pct: Double): Double = interpolateLinear(abvNodes, densityNodes, pct)

    /** Mass fraction of ethanol (0..1) for a given volumetric ABV%. */
    fun massFractionFromAbv(pct: Double): Double {
        val p = pct.coerceIn(0.0, 100.0)
        return (p / 100.0 * 100.0 * ETHANOL_DENSITY_20C) / (100.0 * densityFromAbv(p))
    }

    /**
     * Inverse of [massFractionFromAbv] via binary search; w is a mass fraction in [0, 1].
     * 30 iterations halve the initial [0, 100] range to ~9e-8, far past the ~0.001
     * precision this app needs, without doing double the work like a larger iteration
     * count would.
     */
    fun abvFromMassFraction(w: Double): Double {
        val target = w.coerceIn(0.0, 1.0)
        var lo = 0.0
        var hi = 100.0
        repeat(30) {
            val mid = (lo + hi) / 2.0
            if (massFractionFromAbv(mid) < target) lo = mid else hi = mid
        }
        return (lo + hi) / 2.0
    }
}
