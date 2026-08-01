package com.homedistill.alcoholcalc.data

import androidx.compose.runtime.Immutable

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

/** Language codes accepted by AppCompatDelegate.setApplicationLocales(). */
object Language {
    const val RU = "ru"
    const val EN = "en"
}

/**
 * [visibleTabs] is always replaced wholesale (never mutated in place) by
 * [UserPreferencesRepository], so it's safe to mark this stable for Compose —
 * otherwise the compiler treats the plain [Set] as unstable and skips fewer
 * recompositions than it could.
 */
@Immutable
data class AppSettings(
    val visibleTabs: Set<String> = CalculatorTabIds.ALL,
)
