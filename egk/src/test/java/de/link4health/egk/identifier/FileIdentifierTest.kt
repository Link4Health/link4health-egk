package de.link4health.egk.identifier

import org.junit.Assert.assertArrayEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FileIdentifierTest {

    @Test
    fun validFidFromInt() {
        val fid = FileIdentifier(0x1000)
        assertArrayEquals(byteArrayOf(0x10, 0x00), fid.getFid())
    }

    @Test
    fun validFidUpperBound() {
        val fid = FileIdentifier(0xFEFF)
        assertArrayEquals(byteArrayOf(0xFE.toByte(), 0xFF.toByte()), fid.getFid())
    }

    @Test
    fun specialFid011CIsValid() {
        val fid = FileIdentifier(0x011C)
        assertArrayEquals(byteArrayOf(0x01, 0x1C), fid.getFid())
    }

    @Test
    fun reservedFid3FFFThrows() {
        assertFailsWith<IllegalArgumentException> {
            FileIdentifier(0x3FFF)
        }
    }

    @Test
    fun belowRangeThrows() {
        assertFailsWith<IllegalArgumentException> {
            FileIdentifier(0x0000)
        }
    }

    @Test
    fun aboveRangeThrows() {
        assertFailsWith<IllegalArgumentException> {
            FileIdentifier(0xFF00)
        }
    }

    @Test
    fun byteArrayConstructor() {
        val fid = FileIdentifier(byteArrayOf(0x10, 0x00))
        assertArrayEquals(byteArrayOf(0x10, 0x00), fid.getFid())
    }

    @Test
    fun byteArrayWrongLengthThrows() {
        assertFailsWith<IllegalArgumentException> {
            FileIdentifier(byteArrayOf(0x10))
        }
    }

    @Test
    fun hexStringConstructor() {
        val fid = FileIdentifier("1000")
        assertArrayEquals(byteArrayOf(0x10, 0x00), fid.getFid())
    }
}
