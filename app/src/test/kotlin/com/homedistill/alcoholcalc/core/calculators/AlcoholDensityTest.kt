package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AlcoholDensityTest {

    @Test
    fun `density at exact table nodes matches table`() {
        assertEquals(0.99823, AlcoholDensity.densityFromAbv(0.0), 1e-6)
        assertEquals(0.93518, AlcoholDensity.densityFromAbv(40.0), 1e-6)
        assertEquals(0.78934, AlcoholDensity.densityFromAbv(100.0), 1e-6)
    }

    @Test
    fun `density interpolates linearly between nodes`() {
        // midpoint between 40 (0.93518) and 45 (0.92457)
        val expected = (0.93518 + 0.92457) / 2.0
        assertEquals(expected, AlcoholDensity.densityFromAbv(42.5), 1e-6)
    }

    @Test
    fun `density clamps outside 0-100 range instead of crashing`() {
        assertEquals(0.99823, AlcoholDensity.densityFromAbv(-50.0), 1e-6)
        assertEquals(0.78934, AlcoholDensity.densityFromAbv(500.0), 1e-6)
        assertFalse(AlcoholDensity.densityFromAbv(-50.0).isNaN())
    }

    @Test
    fun `mass fraction round-trips through abvFromMassFraction`() {
        val original = 55.0
        val w = AlcoholDensity.massFractionFromAbv(original)
        val recovered = AlcoholDensity.abvFromMassFraction(w)
        assertEquals(original, recovered, 0.01)
    }

    @Test
    fun `mass fraction is 0 for water and 1 for pure ethanol`() {
        assertEquals(0.0, AlcoholDensity.massFractionFromAbv(0.0), 1e-6)
        assertEquals(1.0, AlcoholDensity.massFractionFromAbv(100.0), 1e-6)
    }
}
