package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SelectionRateCalculatorTest {

    @Test
    fun `500 mL over 10 minutes gives 3000 mL per hour`() {
        val result = calculateSelectionRate(volumeMl = 500.0, timeSeconds = 600.0)
        assertNotNull(result)
        assertEquals(3000.0, result!!.mlPerHour, 1e-6)
        assertEquals(50.0, result.mlPerMinute, 1e-6)
    }

    @Test
    fun `zero or negative time returns null instead of dividing by zero`() {
        assertNull(calculateSelectionRate(volumeMl = 500.0, timeSeconds = 0.0))
        assertNull(calculateSelectionRate(volumeMl = 500.0, timeSeconds = -10.0))
    }

    @Test
    fun `formatMmSs formats seconds and never goes negative`() {
        assertEquals("00:00", formatMmSs(0))
        assertEquals("01:05", formatMmSs(65))
        assertEquals("00:00", formatMmSs(-30))
    }
}
