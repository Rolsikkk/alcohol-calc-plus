package com.homedistill.alcoholcalc.core.calculators

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class HeaterCalculatorTest {

    @Test
    fun `220V 2000W element on a 230V supply`() {
        val result = calculateHeaterPower(ratedVoltage = 220.0, ratedPowerW = 2000.0, realVoltage = 230.0)
        assertNotNull(result)
        result!!

        assertEquals(24.2, result.resistanceOhm, 0.01)
        assertEquals(2185.95, result.realPowerW, 0.5)
        assertEquals(9.5041, result.realCurrentA, 0.01)
    }

    @Test
    fun `undervoltage reduces real power below rated`() {
        val result = calculateHeaterPower(ratedVoltage = 220.0, ratedPowerW = 2000.0, realVoltage = 200.0)!!
        assertEquals(1652.9, result.realPowerW, 1.0)
    }

    @Test
    fun `non-positive rated voltage or power returns null`() {
        assertNull(calculateHeaterPower(ratedVoltage = 0.0, ratedPowerW = 2000.0, realVoltage = 220.0))
        assertNull(calculateHeaterPower(ratedVoltage = -220.0, ratedPowerW = 2000.0, realVoltage = 220.0))
        assertNull(calculateHeaterPower(ratedVoltage = 220.0, ratedPowerW = 0.0, realVoltage = 220.0))
    }
}
