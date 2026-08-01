package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DilutionCalculatorTest {

    @Test
    fun `diluting 600 mL of 96,5 percent spirit to 40 percent matches expected result`() {
        val result = calculateDilution(v1 = 600.0, p1 = 96.5, target = 40.0)
        assertTrue(result is DilutionResult.Success)
        result as DilutionResult.Success

        assertEquals(1448.0, result.resultVolumeMl, 5.0)
        assertEquals(1354.0, result.resultMassG, 5.0)
        assertEquals(40.0, result.resultAbvPct, 1e-9)
        assertEquals(578.999, result.absoluteAlcoholMl, 0.5) // 600 * 96.5 / 100
        assertTrue(result.waterVolumeMl > 0)
        assertEquals(480.0, result.startMassG, 5.0)
        assertEquals(result.resultMassG - result.startMassG, result.waterMassG, 1e-6)
    }

    @Test
    fun `target strength greater or equal to source strength is invalid`() {
        assertEquals(DilutionResult.Invalid, calculateDilution(v1 = 500.0, p1 = 40.0, target = 40.0))
        assertEquals(DilutionResult.Invalid, calculateDilution(v1 = 500.0, p1 = 40.0, target = 60.0))
    }

    @Test
    fun `non-positive inputs are invalid and never produce NaN`() {
        assertEquals(DilutionResult.Invalid, calculateDilution(v1 = 0.0, p1 = 40.0, target = 10.0))
        assertEquals(DilutionResult.Invalid, calculateDilution(v1 = -100.0, p1 = 40.0, target = 10.0))
        assertEquals(DilutionResult.Invalid, calculateDilution(v1 = 500.0, p1 = -5.0, target = 10.0))
        assertEquals(DilutionResult.Invalid, calculateDilution(v1 = 500.0, p1 = 40.0, target = 0.0))
    }
}
