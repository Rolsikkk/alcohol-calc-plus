package com.homedistill.alcoholcalc.core.update

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VersionComparatorTest {

    @Test
    fun `higher minor or patch version is newer`() {
        assertTrue(isNewerVersion(current = "1.0", candidate = "1.1"))
        assertTrue(isNewerVersion(current = "1.0.0", candidate = "1.0.1"))
        assertTrue(isNewerVersion(current = "1.9", candidate = "2.0"))
    }

    @Test
    fun `leading v prefix on tags is ignored`() {
        assertTrue(isNewerVersion(current = "1.0", candidate = "v1.1"))
        assertFalse(isNewerVersion(current = "v1.1", candidate = "v1.1"))
    }

    @Test
    fun `equal or older version is not newer`() {
        assertFalse(isNewerVersion(current = "1.2", candidate = "1.2"))
        assertFalse(isNewerVersion(current = "1.2", candidate = "1.1"))
        assertFalse(isNewerVersion(current = "2.0", candidate = "1.9"))
    }
}
