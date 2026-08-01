package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Test

class NumberFormattingTest {

    @Test
    fun `formats with a dot regardless of default locale`() {
        assertEquals("40.50", formatDecimal(40.5, 2))
        assertEquals("100.00", formatDecimal(100.0, 2))
    }

    @Test
    fun `NaN and infinite values never leak into the UI string`() {
        assertEquals("0", formatDecimal(Double.NaN))
        assertEquals("0", formatDecimal(Double.POSITIVE_INFINITY))
    }
}
