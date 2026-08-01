package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RectificationCalculatorTest {

    @Test
    fun `default 8-60-30 split of 10 L of 40 percent raw spirit`() {
        val result = calculateRectification(v = 10000.0, p = 40.0)
        assertNotNull(result)
        result!!

        assertEquals(4000.0, result.absoluteAlcoholMl, 1e-6) // ac = v * p / 100
        assertEquals(320.0, result.headsMl, 1e-6)
        assertEquals(2400.0, result.bodyMl, 1e-6)
        assertEquals(1200.0, result.tailsMl, 1e-6)
        assertEquals(9351.8, result.rawMassG, 0.5)
    }

    @Test
    fun `custom split percentages are honored`() {
        val result = calculateRectification(v = 1000.0, p = 50.0, headsPct = 10.0, bodyPct = 70.0, tailsPct = 20.0)
        assertNotNull(result)
        result!!

        val ac = 500.0
        assertEquals(ac * 0.10, result.headsMl, 1e-6)
        assertEquals(ac * 0.70, result.bodyMl, 1e-6)
        assertEquals(ac * 0.20, result.tailsMl, 1e-6)
    }

    @Test
    fun `non-positive or out-of-range inputs return null instead of crashing`() {
        assertNull(calculateRectification(v = 0.0, p = 40.0))
        assertNull(calculateRectification(v = -5.0, p = 40.0))
        assertNull(calculateRectification(v = 1000.0, p = 0.0))
        assertNull(calculateRectification(v = 1000.0, p = 150.0))
    }
}
