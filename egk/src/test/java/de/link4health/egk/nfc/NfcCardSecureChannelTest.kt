package de.link4health.egk.nfc

import android.nfc.tech.IsoDep
import de.link4health.egk.card.PaceKey
import de.link4health.egk.card.SecureChannelTerminatedException
import de.link4health.egk.command.CommandApdu
import de.link4health.egk.diagnostics.EgkDiagnosticEvent
import de.link4health.egk.diagnostics.EgkDiagnosticOperation
import de.link4health.egk.diagnostics.EgkDiagnosticStatus
import de.link4health.egk.diagnostics.EgkDiagnostics
import de.link4health.egk.diagnostics.EgkFailureCategory
import de.link4health.egk.tagobjects.MacObject
import de.link4health.egk.tagobjects.StatusObject
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.bouncycastle.util.encoders.Hex
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NfcCardSecureChannelTest {
    @Test
    fun terminalSecureMessagingFailureInvalidatesSession() {
        val diagnostics = RecordingDiagnostics()
        val isoDep =
            mockk<IsoDep>(relaxed = true) {
                every { isConnected } returns true
                every { maxTransceiveLength } returns 4096
                every { transceive(any()) } returns byteArrayOf(0x90.toByte(), 0x00)
                every { close() } just runs
            }
        val secureChannel =
            NfcCardSecureChannel(
                isExtendedLengthSupported = true,
                nfcHealthCard = NfcHealthCard(isoDep, diagnostics),
                paceKey = paceKey(),
                diagnostics = diagnostics,
            )

        assertFailsWith<IllegalArgumentException> {
            secureChannel.transmit(CommandApdu.ofOptions(0x00, 0xA4, 0x04, 0x00, null))
        }

        val terminalError = assertFailsWith<SecureChannelTerminatedException> {
            secureChannel.transmit(CommandApdu.ofOptions(0x00, 0xA4, 0x04, 0x00, null))
        }

        assertNotNull(terminalError.cause)
        assertTrue(
            diagnostics.events.any {
                it.operation == EgkDiagnosticOperation.SECURE_MESSAGING_DECRYPT &&
                    it.status == EgkDiagnosticStatus.FAILED &&
                    it.failureCategory == EgkFailureCategory.MALFORMED_APDU
            },
        )
        assertTrue(
            diagnostics.events.any {
                it.operation == EgkDiagnosticOperation.SECURE_CHANNEL_TRANSMIT &&
                    it.status == EgkDiagnosticStatus.FAILED
            },
        )
        verify(exactly = 1) { isoDep.transceive(any()) }
    }

    @Test
    fun closingOriginalPaceKeyDoesNotInvalidateActiveSecureChannel() {
        val diagnostics = RecordingDiagnostics()
        val paceKey = paceKey()
        val isoDep =
            mockk<IsoDep>(relaxed = true) {
                every { isConnected } returns true
                every { maxTransceiveLength } returns 4096
                // The channel increments the SSC once for encrypt and once for decrypt,
                // so the card response MAC must be computed for SSC = 2
                every { transceive(any()) } returns statusOnlyResponse(paceKey, ssc = 2)
                every { close() } just runs
            }
        val secureChannel =
            NfcCardSecureChannel(
                isExtendedLengthSupported = true,
                nfcHealthCard = NfcHealthCard(isoDep, diagnostics),
                paceKey = paceKey,
                diagnostics = diagnostics,
            )

        paceKey.close()

        val response = secureChannel.transmit(CommandApdu.ofOptions(0x01, 0x02, 0x03, 0x04, null))

        assertEquals(0x9000, response.sw)
        assertTrue(paceKey.enc.all { it == 0.toByte() })
        assertTrue(paceKey.mac.all { it == 0.toByte() })
        verify(exactly = 1) { isoDep.transceive(any()) }
    }

    private fun paceKey(): PaceKey =
        PaceKey(
            Hex.decode("68406B4162100563D9C901A6154D2901"),
            Hex.decode("73FF268784F72AF833FDC9464049AFC9"),
        )

    /**
     * Builds a secure messaging response containing only DO99 (status) and DO8E (MAC)
     * with a MAC valid for the given send sequence counter.
     */
    private fun statusOnlyResponse(paceKey: PaceKey, ssc: Int): ByteArray {
        val sscBytes = ByteArray(16).also { it[15] = ssc.toByte() }
        val statusBytes = byteArrayOf(0x90.toByte(), 0x00)
        val responseData = ByteArrayOutputStream()
        StatusObject(statusBytes).taggedObject.encodeTo(responseData)
        val mac = MacObject(commandOutput = responseData, kMac = paceKey.mac, ssc = sscBytes).mac
        return Hex.decode("990290008E08") + mac + statusBytes
    }

    private class RecordingDiagnostics : EgkDiagnostics {
        val events = mutableListOf<EgkDiagnosticEvent>()

        override fun onEvent(event: EgkDiagnosticEvent) {
            events += event
        }
    }
}
