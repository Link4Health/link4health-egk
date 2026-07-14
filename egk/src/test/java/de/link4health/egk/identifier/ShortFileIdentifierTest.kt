package de.link4health.egk.identifier

import org.junit.Assert.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ShortFileIdentifierTest {

    @Test
    fun validMinValue() {
        val sfid = ShortFileIdentifier(1)
        assertEquals(1, sfid.sfId)
    }

    @Test
    fun validMaxValue() {
        val sfid = ShortFileIdentifier(30)
        assertEquals(30, sfid.sfId)
    }

    @Test
    fun validMidRange() {
        val sfid = ShortFileIdentifier(15)
        assertEquals(15, sfid.sfId)
    }

    @Test
    fun belowMinThrows() {
        assertFailsWith<IllegalArgumentException> {
            ShortFileIdentifier(0)
        }
    }

    @Test
    fun aboveMaxThrows() {
        assertFailsWith<IllegalArgumentException> {
            ShortFileIdentifier(31)
        }
    }

    @Test
    fun negativeValueThrows() {
        assertFailsWith<IllegalArgumentException> {
            ShortFileIdentifier(-1)
        }
    }

    @Test
    fun hexStringConstructor() {
        val sfid = ShortFileIdentifier("0A")
        assertEquals(10, sfid.sfId)
    }
}
