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

import de.link4health.egk.card.BLOCK_SIZE
import de.link4health.egk.card.ICardChannel
import de.link4health.egk.card.PaceKey
import de.link4health.egk.card.SecureChannelTerminatedException
import de.link4health.egk.card.SecureMessaging
import de.link4health.egk.command.CommandApdu
import de.link4health.egk.command.ResponseApdu
import de.link4health.egk.diagnostics.EgkDiagnosticEvent
import de.link4health.egk.diagnostics.EgkDiagnosticOperation
import de.link4health.egk.diagnostics.EgkDiagnosticStatus
import de.link4health.egk.diagnostics.EgkDiagnostics
import de.link4health.egk.diagnostics.elapsedSince
import de.link4health.egk.diagnostics.emitSafely
import de.link4health.egk.diagnostics.toFailureCategory
import io.github.aakira.napier.Napier
import java.io.Closeable
import java.io.IOException

class NfcCardSecureChannel(
    override val isExtendedLengthSupported: Boolean,
    private val nfcHealthCard: NfcHealthCard,
    paceKey: PaceKey,
    val diagnostics: EgkDiagnostics = EgkDiagnostics.NONE,
) : ICardChannel, Closeable {
    private var secureMessaging = SecureMessaging(paceKey, ByteArray(BLOCK_SIZE))

    @Volatile
    private var isClosed = false

    @Volatile
    private var terminalFailure: Throwable? = null

    override val card: NfcHealthCard get() = nfcHealthCard

    override val maxTransceiveLength = card.isoDep.maxTransceiveLength

    /**
     * Returns the responseApdu after transmitting a commandApdu
     */
    @Synchronized
    override fun transmit(command: CommandApdu): ResponseApdu {
        val commandLength = command.size
        val startNanos = System.nanoTime()
        diagnostics.emitSafely(
            EgkDiagnosticEvent(
                operation = EgkDiagnosticOperation.SECURE_CHANNEL_TRANSMIT,
                status = EgkDiagnosticStatus.STARTED,
                commandLength = commandLength,
                isSecureChannel = true,
            ),
        )

        try {
            ensureUsable()

            Napier.d("Encrypt ----")
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.SECURE_MESSAGING_ENCRYPT,
                    status = EgkDiagnosticStatus.STARTED,
                    commandLength = commandLength,
                    isSecureChannel = true,
                ),
            )
            val encryptedCommand =
                try {
                    secureMessaging.encrypt(command).also {
                        diagnostics.emitSafely(
                            EgkDiagnosticEvent(
                                operation = EgkDiagnosticOperation.SECURE_MESSAGING_ENCRYPT,
                                status = EgkDiagnosticStatus.SUCCEEDED,
                                commandLength = commandLength,
                                responseLength = it.bytes.size,
                                isSecureChannel = true,
                            ),
                        )
                    }
                } catch (t: Throwable) {
                    diagnostics.emitSafely(
                        EgkDiagnosticEvent(
                            operation = EgkDiagnosticOperation.SECURE_MESSAGING_ENCRYPT,
                            status = EgkDiagnosticStatus.FAILED,
                            commandLength = commandLength,
                            isSecureChannel = true,
                            failureCategory = t.toFailureCategory(),
                            failureType = t::class.simpleName,
                        ),
                    )
                    throw t
                }

            require(encryptedCommand.size <= maxTransceiveLength) {
                "Encrypted CommandApdu is too long to send. Limit for reader is $maxTransceiveLength " +
                    "but encrypted length is ${encryptedCommand.size}"
            }

            Napier.d("encrypted ----")
            val encryptedResponse = nfcHealthCard.transmit(encryptedCommand)

            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.SECURE_MESSAGING_DECRYPT,
                    status = EgkDiagnosticStatus.STARTED,
                    responseLength = encryptedResponse.bytes.size,
                    isSecureChannel = true,
                ),
            )

            val response =
                try {
                    encryptedResponse.let {
                        Napier.d("Decrypt ----")
                        secureMessaging.decrypt(encryptedResponse)
                    }.also {
                        diagnostics.emitSafely(
                            EgkDiagnosticEvent(
                                operation = EgkDiagnosticOperation.SECURE_MESSAGING_DECRYPT,
                                status = EgkDiagnosticStatus.SUCCEEDED,
                                responseLength = it.bytes.size,
                                isSecureChannel = true,
                            ),
                        )
                    }
                } catch (t: Throwable) {
                    diagnostics.emitSafely(
                        EgkDiagnosticEvent(
                            operation = EgkDiagnosticOperation.SECURE_MESSAGING_DECRYPT,
                            status = EgkDiagnosticStatus.FAILED,
                            responseLength = encryptedResponse.bytes.size,
                            isSecureChannel = true,
                            failureCategory = t.toFailureCategory(),
                            failureType = t::class.simpleName,
                        ),
                    )
                    throw t
                }
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.SECURE_CHANNEL_TRANSMIT,
                    status = EgkDiagnosticStatus.SUCCEEDED,
                    durationMillis = elapsedSince(startNanos),
                    commandLength = commandLength,
                    responseLength = response.bytes.size,
                    isSecureChannel = true,
                ),
            )
            return response
        } catch (t: Throwable) {
            terminalFailure = t
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.SECURE_CHANNEL_TRANSMIT,
                    status = EgkDiagnosticStatus.FAILED,
                    durationMillis = elapsedSince(startNanos),
                    commandLength = commandLength,
                    isSecureChannel = true,
                    failureCategory = t.toFailureCategory(),
                    failureType = t::class.simpleName,
                ),
            )
            throw t
        }
    }

    @Synchronized
    override fun close() {
        if (isClosed) return
        val startNanos = System.nanoTime()
        diagnostics.emitSafely(
            EgkDiagnosticEvent(
                operation = EgkDiagnosticOperation.CHANNEL_CLOSE,
                status = EgkDiagnosticStatus.STARTED,
                isSecureChannel = true,
            ),
        )
        isClosed = true
        try {
            if (nfcHealthCard.isoDep.isConnected) {
                nfcHealthCard.isoDep.close()
            }
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.CHANNEL_CLOSE,
                    status = EgkDiagnosticStatus.SUCCEEDED,
                    durationMillis = elapsedSince(startNanos),
                    isSecureChannel = true,
                ),
            )
        } catch (e: IOException) {
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.CHANNEL_CLOSE,
                    status = EgkDiagnosticStatus.FAILED,
                    durationMillis = elapsedSince(startNanos),
                    isSecureChannel = true,
                    failureCategory = de.link4health.egk.diagnostics.EgkFailureCategory.TRANSPORT_IO,
                    failureType = e::class.simpleName,
                ),
            )
            // Cleanup failures are observable through diagnostics but must not escape close().
        } finally {
            secureMessaging.close()
        }
    }

    private fun ensureUsable() {
        if (isClosed || !card.isoDep.isConnected) {
            throw NfcChannelClosedException()
        }
        terminalFailure?.let {
            throw SecureChannelTerminatedException(
                "Secure channel session is no longer usable after a previous failure",
                it,
            )
        }
    }
}
