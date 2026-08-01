package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ThermometerCalculatorTest {

    @Test
    fun `at calibration temperature the reading is unchanged`() {
        assertEquals(55.0, correctAlcoholmeterReading(tempC = 20.0, apparentPct = 55.0), 1e-9)
    }

    @Test
    fun `warmer sample reads lower true strength`() {
        // dT=5, correction = 5 * (0.3 + 0.004*40) = 2.3
        assertEquals(37.7, correctAlcoholmeterReading(tempC = 25.0, apparentPct = 40.0), 1e-6)
    }

    @Test
    fun `result is clamped to 0-100 and never NaN`() {
        val real = correctAlcoholmeterReading(tempC = 60.0, apparentPct = 5.0)
        assertEquals(0.0, real, 1e-9)
        assertFalse(real.isNaN())

        val highTemp = correctAlcoholmeterReading(tempC = -40.0, apparentPct = 99.0)
        assertEquals(100.0, highTemp, 1e-9)
    }
}
