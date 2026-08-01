package com.homedistill.alcoholcalc.core.calculators

data class SelectionRate(val mlPerHour: Double, val mlPerMinute: Double)

/** Distillate collection rate from a sampled volume/time. Null when [timeSeconds] <= 0. */
fun calculateSelectionRate(volumeMl: Double, timeSeconds: Double): SelectionRate? {
    if (timeSeconds <= 0.0) return null
    val perHour = volumeMl / timeSeconds * 3600.0
    val perMinute = volumeMl / timeSeconds * 60.0
    return SelectionRate(perHour, perMinute)
}

/** Formats a non-negative duration as mm:ss (minutes can exceed 59). */
fun formatMmSs(totalSeconds: Long): String {
    val safeSeconds = totalSeconds.coerceAtLeast(0L)
    val minutes = safeSeconds / 60
    val seconds = safeSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
