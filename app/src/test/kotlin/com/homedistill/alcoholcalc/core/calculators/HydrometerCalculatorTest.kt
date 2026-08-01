package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Test

class HydrometerCalculatorTest {

    @Test
    fun `at calibration temperature sg20 equals the raw reading`() {
        val result = correctHydrometerReading(tempC = 20.0, sg = 1040.0)
        assertEquals(10.0, result.brix, 1e-9)
        assertEquals(1040.0, result.sg20, 1e-9)
        assertEquals(10.0, result.brix20, 1e-9)
    }

    @Test
    fun `warmer sample corrects sg upward`() {
        val result = correctHydrometerReading(tempC = 30.0, sg = 1050.0)
        assertEquals(12.5, result.brix, 1e-9)
        assertEquals(1052.0, result.sg20, 1e-9)
        assertEquals(13.0, result.brix20, 1e-9)
    }

    @Test
    fun `zero and negative sg values do not crash`() {
        val result = correctHydrometerReading(tempC = 20.0, sg = 0.0)
        assertEquals(-250.0, result.brix, 1e-9)
        assertEquals(0.0, result.sg20, 1e-9)
    }
}
