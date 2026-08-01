package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MixingCalculatorTest {

    @Test
    fun `mixing 1 L of 40 percent with 1 L of water yields about 20 percent`() {
        val result = calculateMixing(listOf(SolutionInput(1000.0, 40.0), SolutionInput(1000.0, 0.0)))

        assertNotNull(result.total)
        val total = result.total!!

        assertEquals(1933.4, total.totalMassG, 2.0)
        assertEquals(20.0, total.resultAbvPct, 1.0)
        // contraction: resulting volume must be less than the naive 2000 mL sum
        assertTrue(total.resultVolumeMl < 2000.0)
        assertEquals(2, result.perSolutionMassG.size)
    }

    @Test
    fun `solutions with non-positive volume are treated as absent`() {
        val result = calculateMixing(
            listOf(SolutionInput(-100.0, 50.0), SolutionInput(500.0, 40.0), SolutionInput(0.0, 96.0))
        )

        assertEquals(0.0, result.perSolutionMassG[0], 1e-9)
        assertEquals(0.0, result.perSolutionMassG[2], 1e-9)
        assertNotNull(result.total)
    }

    @Test
    fun `all volumes non-positive clears the result instead of keeping stale data`() {
        val result = calculateMixing(listOf(SolutionInput(0.0, 40.0), SolutionInput(-10.0, 96.0)))

        assertNull(result.total)
        assertEquals(listOf(0.0, 0.0), result.perSolutionMassG)
    }
}
