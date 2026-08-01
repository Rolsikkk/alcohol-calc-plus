package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NumberParsingTest {

    @Test
    fun `accepts both comma and dot as decimal separator`() {
        assertEquals(40.5, parseDecimalInput("40,5")!!, 1e-9)
        assertEquals(40.5, parseDecimalInput("40.5")!!, 1e-9)
    }

    @Test
    fun `blank or non-numeric input returns null instead of crashing`() {
        assertNull(parseDecimalInput(""))
        assertNull(parseDecimalInput("   "))
        assertNull(parseDecimalInput("abc"))
    }
}
