package de.link4health.egk.identifier

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ApplicationIdentifierTest {

    @Test
    fun validMinLengthAid() {
        val aid = ApplicationIdentifier(ByteArray(5) { 0x01 })
        assertEquals(5, aid.aidValue.size)
    }

    @Test
    fun validMaxLengthAid() {
        val aid = ApplicationIdentifier(ByteArray(16) { 0x02 })
        assertEquals(16, aid.aidValue.size)
    }

    @Test
    fun aidValueReturnsCopy() {
        val bytes = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        val aid = ApplicationIdentifier(bytes)
        val copy = aid.aidValue
        copy[0] = 0xFF.toByte()
        assertArrayEquals(bytes, aid.aidValue)
    }

    @Test
    fun tooShortAidThrows() {
        assertFailsWith<IllegalArgumentException> {
            ApplicationIdentifier(ByteArray(4) { 0x01 })
        }
    }

    @Test
    fun tooLongAidThrows() {
        assertFailsWith<IllegalArgumentException> {
            ApplicationIdentifier(ByteArray(17) { 0x01 })
        }
    }

    @Test
    fun hexStringConstructor() {
        val aid = ApplicationIdentifier("D276000141")
        assertEquals(5, aid.aidValue.size)
        assertArrayEquals(
            byteArrayOf(0xD2.toByte(), 0x76, 0x00, 0x01, 0x41),
            aid.aidValue,
        )
    }
}
