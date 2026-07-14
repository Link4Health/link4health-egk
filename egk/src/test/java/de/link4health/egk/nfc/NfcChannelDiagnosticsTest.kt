package de.link4health.egk.nfc

import android.nfc.tech.IsoDep
import de.link4health.egk.command.CommandApdu
import de.link4health.egk.diagnostics.EgkDiagnosticEvent
import de.link4health.egk.diagnostics.EgkDiagnosticOperation
import de.link4health.egk.diagnostics.EgkDiagnosticStatus
import de.link4health.egk.diagnostics.EgkDiagnostics
import de.link4health.egk.diagnostics.EgkFailureCategory
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import java.io.InterruptedIOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NfcChannelDiagnosticsTest {
    @Test
    fun transmitEmitsStructuredSuccessDiagnostics() {
        val diagnostics = RecordingDiagnostics()
        val isoDep = mockIsoDep(
            transceiveResponse = byteArrayOf(0x90.toByte(), 0x00),
        )
        val healthCard = NfcHealthCard(isoDep, diagnostics)

        val response = healthCard.transmit(CommandApdu.ofOptions(0x00, 0xA4, 0x04, 0x00, null))

        assertEquals(0x9000, response.sw)
        assertEquals(
            listOf(EgkDiagnosticStatus.STARTED, EgkDiagnosticStatus.SUCCEEDED),
            diagnostics.events.map { it.status },
        )
        assertEquals(EgkDiagnosticOperation.APDU_TRANSMIT, diagnostics.events.first().operation)
        assertEquals(4, diagnostics.events.first().commandLength)
        assertEquals(2, diagnostics.events.last().responseLength)
    }

    @Test
    fun transmitOnDisconnectedIsoDepThrowsTypedExceptionAndEmitsFailureDiagnostics() {
        val diagnostics = RecordingDiagnostics()
        val isoDep = mockIsoDep(isConnected = false)
        val healthCard = NfcHealthCard(isoDep, diagnostics)

        val error = assertFailsWith<NfcTransmitException> {
            healthCard.transmit(CommandApdu.ofOptions(0x00, 0xA4, 0x04, 0x00, null))
        }

        assertEquals(EgkFailureCategory.CHANNEL_CLOSED, error.category)
        assertEquals(
            listOf(EgkDiagnosticStatus.STARTED, EgkDiagnosticStatus.FAILED),
            diagnostics.events.map { it.status },
        )
        assertEquals(EgkFailureCategory.CHANNEL_CLOSED, diagnostics.events.last().failureCategory)
    }

    @Test
    fun transmitTimeoutReportsTimeoutInsteadOfTagLoss() {
        val diagnostics = RecordingDiagnostics()
        val isoDep = mockIsoDep()
        every { isoDep.transceive(any()) } throws InterruptedIOException("timed out")
        val healthCard = NfcHealthCard(isoDep, diagnostics)

        val error = assertFailsWith<NfcTransmitException> {
            healthCard.transmit(CommandApdu.ofOptions(0x00, 0xA4, 0x04, 0x00, null))
        }

        assertEquals(EgkFailureCategory.TIMEOUT, error.category)
        assertEquals("NFC transceive timed out", error.message)
        assertEquals(EgkFailureCategory.TIMEOUT, diagnostics.events.last().failureCategory)
    }

    @Test
    fun closeIsIdempotentAndClosesIsoDepOnce() {
        val diagnostics = RecordingDiagnostics()
        val isoDep = mockIsoDep()
        val channel = NfcCardChannel(true, NfcHealthCard(isoDep, diagnostics), diagnostics)

        channel.close()
        channel.close()

        verify(exactly = 1) { isoDep.close() }
        assertEquals(
            listOf(EgkDiagnosticStatus.STARTED, EgkDiagnosticStatus.SUCCEEDED),
            diagnostics.events.map { it.status },
        )
        assertEquals(EgkDiagnosticOperation.CHANNEL_CLOSE, diagnostics.events.first().operation)
    }

    private fun mockIsoDep(
        isConnected: Boolean = true,
        maxTransceiveLength: Int = 4096,
        transceiveResponse: ByteArray = byteArrayOf(0x90.toByte(), 0x00),
    ): IsoDep =
        mockk(relaxed = true) {
            every { this@mockk.isConnected } returns isConnected
            every { this@mockk.maxTransceiveLength } returns maxTransceiveLength
            every { this@mockk.transceive(any()) } returns transceiveResponse
            every { this@mockk.close() } just runs
        }

    private class RecordingDiagnostics : EgkDiagnostics {
        val events = mutableListOf<EgkDiagnosticEvent>()

        override fun onEvent(event: EgkDiagnosticEvent) {
            events += event
        }
    }
}
