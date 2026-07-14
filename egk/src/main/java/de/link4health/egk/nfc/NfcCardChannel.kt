/*
 * Copyright (c) 2024 gematik GmbH
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the Licence);
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */
package de.link4health.egk.nfc

import de.link4health.egk.card.ICardChannel
import de.link4health.egk.command.CommandApdu
import de.link4health.egk.command.ResponseApdu
import de.link4health.egk.diagnostics.EgkDiagnosticEvent
import de.link4health.egk.diagnostics.EgkDiagnosticOperation
import de.link4health.egk.diagnostics.EgkDiagnosticStatus
import de.link4health.egk.diagnostics.EgkDiagnostics
import de.link4health.egk.diagnostics.elapsedSince
import de.link4health.egk.diagnostics.emitSafely
import java.io.Closeable
import java.io.IOException

class NfcCardChannel internal constructor(
    override val isExtendedLengthSupported: Boolean,
    private val nfcHealthCard: NfcHealthCard,
    val diagnostics: EgkDiagnostics = EgkDiagnostics.NONE,
) : ICardChannel, Closeable {
    @Volatile
    private var isClosed = false

    override val card: NfcHealthCard
        get() = nfcHealthCard

    override val maxTransceiveLength = card.isoDep.maxTransceiveLength

    /**
     * Returns the responseApdu after transmitting a commandApdu
     */
    @Synchronized
    override fun transmit(command: CommandApdu): ResponseApdu {
        ensureOpen()
        return nfcHealthCard.transmit(command)
    }

    @Synchronized
    override fun close() {
        if (isClosed) return
        val startNanos = System.nanoTime()
        diagnostics.emitSafely(
            EgkDiagnosticEvent(
                operation = EgkDiagnosticOperation.CHANNEL_CLOSE,
                status = EgkDiagnosticStatus.STARTED,
                isSecureChannel = false,
            ),
        )
        isClosed = true
        try {
            if (card.isoDep.isConnected) {
                card.isoDep.close()
            }
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.CHANNEL_CLOSE,
                    status = EgkDiagnosticStatus.SUCCEEDED,
                    durationMillis = elapsedSince(startNanos),
                    isSecureChannel = false,
                ),
            )
        } catch (e: IOException) {
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.CHANNEL_CLOSE,
                    status = EgkDiagnosticStatus.FAILED,
                    durationMillis = elapsedSince(startNanos),
                    isSecureChannel = false,
                    failureCategory = de.link4health.egk.diagnostics.EgkFailureCategory.TRANSPORT_IO,
                    failureType = e::class.simpleName,
                ),
            )
            // Cleanup failures are observable through diagnostics but must not turn an already
            // completed card operation into a failure.
        }
    }

    private fun ensureOpen() {
        if (isClosed || !card.isoDep.isConnected) {
            throw NfcChannelClosedException()
        }
    }
}
