package de.link4health.egk.card

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("FunctionNaming")
class HealthCardVersion2ExtensionTest {
    @Test
    fun `missing object system version bytes returns false`() {
        assertFalse(version(byteArrayOf()).isEGK21())
        assertFalse(version(byteArrayOf(4, 4)).isEGK21())
    }

    @Test
    fun `patch byte participates in version comparison`() {
        assertFalse(version(byteArrayOf(4, 3, 0x7f)).isEGK21())
        assertTrue(version(byteArrayOf(4, 4, 0)).isEGK21())
    }

    private fun version(objectSystemVersion: ByteArray) = HealthCardVersion2(
        fillingInstructionsVersion = byteArrayOf(),
        objectSystemVersion = objectSystemVersion,
        productIdentificationObjectSystemVersion = byteArrayOf(),
        fillingInstructionsEfGdoVersion = byteArrayOf(),
        fillingInstructionsEfAtrVersion = byteArrayOf(),
        fillingInstructionsEfKeyInfoVersion = byteArrayOf(),
        fillingInstructionsEfEnvironmentSettingsVersion = byteArrayOf(),
        fillingInstructionsEfLoggingVersion = byteArrayOf(),
    )
}
