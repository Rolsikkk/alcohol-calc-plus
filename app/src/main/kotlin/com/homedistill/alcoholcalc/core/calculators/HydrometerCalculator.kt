package com.homedistill.alcoholcalc.core.calculators

data class HydrometerResult(
    val brix: Double,
    val sg20: Double,
    val brix20: Double,
)

/**
 * Corrects a hydrometer reading ([sg], in SG x1000 units, e.g. 1040) for sample temperature [tempC].
 * The temperature coefficient (0.2 SGx1000 per °C) is an approximation, not a lab-grade table.
 */
fun correctHydrometerReading(tempC: Double, sg: Double): HydrometerResult {
    val brix = (sg - 1000.0) / 4.0
    val dT = tempC - 20.0
    val k = 0.2
    val sg20 = sg + k * dT
    val brix20 = (sg20 - 1000.0) / 4.0
    return HydrometerResult(brix, sg20, brix20)
}
