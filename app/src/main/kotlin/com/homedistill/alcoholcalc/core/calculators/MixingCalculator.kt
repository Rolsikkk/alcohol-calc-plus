package com.homedistill.alcoholcalc.core.calculators

data class SolutionInput(val volumeMl: Double, val abvPct: Double)

data class MixingTotal(
    val totalMassG: Double,
    val resultAbvPct: Double,
    val resultVolumeMl: Double,
)

data class MixingResult(
    /** Mass (g) contributed by each input solution, in the same order as the input list. */
    val perSolutionMassG: List<Double>,
    /** Null when the combined mass is zero or less — UI should clear result fields. */
    val total: MixingTotal?,
)

/**
 * Mixes 2-3 alcohol-water solutions and computes the resulting volume/strength.
 * Solutions with a non-positive volume are treated as absent (mass = 0) rather than errors.
 */
fun calculateMixing(solutions: List<SolutionInput>): MixingResult {
    val masses = solutions.map { s ->
        if (s.volumeMl <= 0.0) 0.0 else s.volumeMl * AlcoholDensity.densityFromAbv(s.abvPct)
    }
    val totalMass = masses.sum()

    if (totalMass <= 0.0) {
        return MixingResult(perSolutionMassG = masses, total = null)
    }

    val totalEth = solutions.indices.sumOf { i ->
        val s = solutions[i]
        if (s.volumeMl <= 0.0) 0.0 else masses[i] * AlcoholDensity.massFractionFromAbv(s.abvPct)
    }

    val wFinal = totalEth / totalMass
    val pFinal = AlcoholDensity.abvFromMassFraction(wFinal)
    val densFinal = AlcoholDensity.densityFromAbv(pFinal)
    val volFinal = totalMass / densFinal

    return MixingResult(
        perSolutionMassG = masses,
        total = MixingTotal(totalMass, pFinal, volFinal),
    )
}
