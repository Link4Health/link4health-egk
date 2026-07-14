package de.link4health.egk

import de.link4health.egk.Bytes.bigIntToByteArray
import de.link4health.egk.Bytes.padData
import de.link4health.egk.Bytes.unPadData
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import java.math.BigInteger
import kotlin.test.Test

class BytesTest {

    @Test
    fun padDataAlignsToBockBoundary() {
        val data = byteArrayOf(0x01, 0x02, 0x03)
        val padded = padData(data, 16)
        assertEquals(0, padded.size % 16)
        assertEquals(16, padded.size)
    }

    @Test
    fun padDataAddsFullBlockWhenAlreadyAligned() {
        val data = ByteArray(16) { 0x01 }
        val padded = padData(data, 16)
        assertEquals(32, padded.size)
        assertEquals(0x80.toByte(), padded[16])
    }

    @Test
    fun padDataPlacesPadMarkerAfterData() {
        val data = byteArrayOf(0x0A, 0x0B)
        val padded = padData(data, 8)
        assertEquals(0x0A.toByte(), padded[0])
        assertEquals(0x0B.toByte(), padded[1])
        assertEquals(0x80.toByte(), padded[2])
        for (i in 3 until 8) {
            assertEquals(0x00.toByte(), padded[i])
        }
    }

    @Test
    fun unPadDataRemovesPadding() {
        val padded = byteArrayOf(0x01, 0x02, 0x80.toByte(), 0x00, 0x00)
        val result = unPadData(padded)
        assertArrayEquals(byteArrayOf(0x01, 0x02), result)
    }

    @Test
    fun unPadDataReturnsOriginalWhenNoPadMarker() {
        val data = byteArrayOf(0x01, 0x02, 0x03)
        val result = unPadData(data)
        assertArrayEquals(data, result)
    }

    @Test
    fun padAndUnpadRoundTrip() {
        val original = byteArrayOf(0x11, 0x22, 0x33, 0x44, 0x55)
        val padded = padData(original, 16)
        val restored = unPadData(padded)
        assertArrayEquals(original, restored)
    }

    @Test
    fun bigIntToByteArrayStripsLeadingZero() {
        val bi = BigInteger(1, byteArrayOf(0x00, 0x7F))
        val result = bigIntToByteArray(bi)
        assertArrayEquals(byteArrayOf(0x7F), result)
    }

    @Test
    fun bigIntToByteArrayPreservesHighBit() {
        val bi = BigInteger(1, byteArrayOf(0xFF.toByte(), 0x01))
        val result = bigIntToByteArray(bi)
        assertArrayEquals(byteArrayOf(0xFF.toByte(), 0x01), result)
    }

    @Test
    fun padDataEmptyInput() {
        val data = ByteArray(0)
        val padded = padData(data, 16)
        assertEquals(16, padded.size)
        assertEquals(0x80.toByte(), padded[0])
    }
}
