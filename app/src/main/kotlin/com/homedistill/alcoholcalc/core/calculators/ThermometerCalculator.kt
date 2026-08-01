package com.homedistill.alcoholcalc.core.calculators

/**
 * Corrects an alcoholmeter reading (calibrated at 20°C) for the actual sample temperature.
 */
fun correctAlcoholmeterReading(tempC: Double, apparentPct: Double): Double {
    val dT = tempC - 20.0
    val correction = dT * (0.3 + 0.004 * apparentPct)
    return (apparentPct - correction).coerceIn(0.0, 100.0)
}
