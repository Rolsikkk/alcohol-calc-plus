package com.homedistill.alcoholcalc.data

/** All eight calculator tab ids shown on the home screen, used as DataStore keys. */
object CalculatorTabIds {
    const val DILUTION = "dilution"
    const val RECTIFICATION = "rectification"
    const val MIXING = "mixing"
    const val TIMER = "timer"
    const val THERMOMETER = "thermometer"
    const val HYDROMETER = "hydrometer"
    const val PRESSURE = "pressure"
    const val HEATER = "heater"

    val ALL = setOf(DILUTION, RECTIFICATION, MIXING, TIMER, THERMOMETER, HYDROMETER, PRESSURE, HEATER)

    /** Always shown on the home screen, cannot be hidden from Settings. */
    val LOCKED = setOf(DILUTION)
}

object Language {
    const val RU = "ru"
    const val EN = "en"
}

data class AppSettings(
    val visibleTabs: Set<String> = CalculatorTabIds.ALL,
    val language: String = Language.RU,
)
