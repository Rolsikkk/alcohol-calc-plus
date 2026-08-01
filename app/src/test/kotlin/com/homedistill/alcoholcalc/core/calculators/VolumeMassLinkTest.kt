package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Test

class VolumeMassLinkTest {

    @Test
    fun `mass and volume round-trip at a fixed ABV`() {
        val mass = massFromVolume(volumeMl = 600.0, abvPct = 96.0)
        val volumeBack = volumeFromMass(mass, abvPct = 96.0)
        assertEquals(600.0, volumeBack, 1e-6)
    }

    @Test
    fun `mass of water at 0 percent equals its water density`() {
        assertEquals(998.23, massFromVolume(volumeMl = 1000.0, abvPct = 0.0), 0.01)
    }

    @Test
    fun `volumeFromMass never divides by zero for out-of-range ABV`() {
        val volume = volumeFromMass(massG = 500.0, abvPct = -10.0)
        assertEquals(volume, volume, 0.0) // not NaN
        assert(!volume.isNaN() && !volume.isInfinite())
    }
}
