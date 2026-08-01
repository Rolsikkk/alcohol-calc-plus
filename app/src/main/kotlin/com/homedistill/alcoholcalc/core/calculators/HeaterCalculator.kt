package com.homedistill.alcoholcalc.core.calculators

data class HeaterResult(
    val resistanceOhm: Double,
    val realPowerW: Double,
    val realCurrentA: Double,
)

/**
 * Estimates actual heater output when supply voltage differs from the rated voltage,
 * assuming constant resistance (cold-resistance approximation for a resistive heating element).
 */
fun calculateHeaterPower(ratedVoltage: Double, ratedPowerW: Double, realVoltage: Double): HeaterResult? {
    if (ratedVoltage <= 0.0 || ratedPowerW <= 0.0) return null

    val resistance = ratedVoltage * ratedVoltage / ratedPowerW
    val realPower = realVoltage * realVoltage / resistance
    val realCurrent = realVoltage / resistance

    return HeaterResult(resistance, realPower, realCurrent)
}
