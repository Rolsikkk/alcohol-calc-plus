package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PressureCalculatorTest {

    @Test
    fun `at atmospheric pressure and 40 percent cube strength gives expected boil point`() {
        val result = calculatePressureCorrection(pressureMmHg = 760.0, cubeAbvPct = 40.0)
        assertNotNull(result)
        result!!

        assertEquals(84.1, result.boilingTempC, 1.5)
        assertEquals(65.5, result.vaporAbvPct, 3.0)
    }

    @Test
    fun `lower pressure lowers the boiling point`() {
        val atAtmospheric = calculatePressureCorrection(pressureMmHg = 760.0, cubeAbvPct = 40.0)!!
        val underVacuum = calculatePressureCorrection(pressureMmHg = 400.0, cubeAbvPct = 40.0)!!

        assertTrue(underVacuum.boilingTempC < atAtmospheric.boilingTempC)
    }

    @Test
    fun `cube strength outside 0-100 is invalid`() {
        assertNull(calculatePressureCorrection(pressureMmHg = 760.0, cubeAbvPct = -1.0))
        assertNull(calculatePressureCorrection(pressureMmHg = 760.0, cubeAbvPct = 101.0))
        assertNull(calculatePressureCorrection(pressureMmHg = 760.0, cubeAbvPct = Double.NaN))
    }
}
