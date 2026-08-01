package com.homedistill.alcoholcalc.core.calculators

/**
 * Piecewise-linear interpolation over a table of (x, y) nodes.
 * Values outside the node range are clamped to the nearest edge value.
 */
internal fun interpolateLinear(xNodes: DoubleArray, yNodes: DoubleArray, x: Double): Double {
    require(xNodes.size == yNodes.size && xNodes.isNotEmpty())
    val clampedX = x.coerceIn(xNodes.first(), xNodes.last())
    if (clampedX <= xNodes[0]) return yNodes[0]
    for (i in 0 until xNodes.size - 1) {
        val x0 = xNodes[i]
        val x1 = xNodes[i + 1]
        if (clampedX <= x1) {
            if (x1 == x0) return yNodes[i]
            val t = (clampedX - x0) / (x1 - x0)
            return yNodes[i] + t * (yNodes[i + 1] - yNodes[i])
        }
    }
    return yNodes.last()
}
