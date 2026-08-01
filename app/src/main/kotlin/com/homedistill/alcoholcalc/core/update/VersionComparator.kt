package com.homedistill.alcoholcalc.core.update

/**
 * Compares two dotted version strings (e.g. "1.2.3", with an optional leading "v",
 * as used in GitHub release tags). Missing trailing components are treated as 0.
 */
fun isNewerVersion(current: String, candidate: String): Boolean {
    val currentParts = normalize(current)
    val candidateParts = normalize(candidate)
    val length = maxOf(currentParts.size, candidateParts.size)
    for (i in 0 until length) {
        val c = currentParts.getOrElse(i) { 0 }
        val n = candidateParts.getOrElse(i) { 0 }
        if (n != c) return n > c
    }
    return false
}

private fun normalize(version: String): List<Int> =
    version.trim()
        .removePrefix("v")
        .removePrefix("V")
        .split(".", "-", "+")
        .mapNotNull { it.toIntOrNull() }
