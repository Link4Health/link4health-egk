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

import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import de.link4health.egk.card.IHealthCard
import de.link4health.egk.command.CommandApdu
import de.link4health.egk.command.ResponseApdu
import de.link4health.egk.diagnostics.EgkDiagnosticEvent
import de.link4health.egk.diagnostics.EgkDiagnosticOperation
import de.link4health.egk.diagnostics.EgkDiagnosticStatus
import de.link4health.egk.diagnostics.EgkDiagnostics
import de.link4health.egk.diagnostics.EgkFailureCategory
import de.link4health.egk.diagnostics.elapsedSince
import de.link4health.egk.diagnostics.emitSafely
import io.github.aakira.napier.Napier
import java.io.IOException
import java.io.InterruptedIOException

class NfcHealthCard internal constructor(
    val isoDep: IsoDep,
    val diagnostics: EgkDiagnostics = EgkDiagnostics.NONE,
) : IHealthCard {

    override fun transmit(apduCommand: CommandApdu): ResponseApdu {
        val commandBytes = apduCommand.bytes
        val startNanos = System.nanoTime()
        diagnostics.emitSafely(
            EgkDiagnosticEvent(
                operation = EgkDiagnosticOperation.APDU_TRANSMIT,
                status = EgkDiagnosticStatus.STARTED,
                commandLength = commandBytes.size,
                isSecureChannel = false,
            ),
        )
        Napier.d("transceive ----")
        val resp = try {
            ensureConnected("NFC transceive attempted on a disconnected IsoDep")
            ResponseApdu(isoDep.transceive(commandBytes))
        } catch (e: SecurityException) {
            reportTransmitFailure(e, EgkFailureCategory.DISCONNECTED, startNanos, commandBytes.size)
        } catch (e: IllegalStateException) {
            reportTransmitFailure(e, EgkFailureCategory.DISCONNECTED, startNanos, commandBytes.size)
        } catch (e: IOException) {
            reportTransmitFailure(e, failureCategoryOf(e), startNanos, commandBytes.size)
        }
        Napier.d("transceived ----")
        diagnostics.emitSafely(
            EgkDiagnosticEvent(
                operation = EgkDiagnosticOperation.APDU_TRANSMIT,
                status = EgkDiagnosticStatus.SUCCEEDED,
                durationMillis = elapsedSince(startNanos),
                commandLength = commandBytes.size,
                responseLength = resp.bytes.size,
                isSecureChannel = false,
            ),
        )
        return resp
    }

    private fun reportTransmitFailure(error: Exception, category: EgkFailureCategory, startNanos: Long, commandLength: Int): Nothing {
        val message = when (category) {
            EgkFailureCategory.TIMEOUT -> "NFC transceive timed out"
            EgkFailureCategory.DISCONNECTED,
            EgkFailureCategory.CHANNEL_CLOSED,
            -> "NFC tag is no longer available"
            else -> "NFC transceive failed"
        }
        val failure = NfcTransmitException(message, error, category)
        diagnostics.emitSafely(
            EgkDiagnosticEvent(
                operation = EgkDiagnosticOperation.APDU_TRANSMIT,
                status = EgkDiagnosticStatus.FAILED,
                durationMillis = elapsedSince(startNanos),
                commandLength = commandLength,
                isSecureChannel = false,
                failureCategory = category,
                failureType = error::class.simpleName,
            ),
        )
        throw failure
    }

    private fun ensureConnected(message: String) {
        if (!isoDep.isConnected) {
            throw NfcChannelClosedException(message)
        }
    }

    companion object {
        private const val ISO_DEP_TIMEOUT = 2500

        fun connect(tag: Tag): NfcCardChannel {
            return connect(tag, diagnostics = EgkDiagnostics.NONE, timeoutMillis = ISO_DEP_TIMEOUT)
        }

        fun connect(
            tag: Tag,
            diagnostics: EgkDiagnostics = EgkDiagnostics.NONE,
            timeoutMillis: Int = ISO_DEP_TIMEOUT,
        ): NfcCardChannel {
            val startNanos = System.nanoTime()
            diagnostics.emitSafely(
                EgkDiagnosticEvent(
                    operation = EgkDiagnosticOperation.NFC_CONNECT,
                    status = EgkDiagnosticStatus.STARTED,
                    isSecureChannel = false,
                ),
            )

            val isoDep = try {
                IsoDep.get(tag) ?: throw NfcChannelConnectException("The provided NFC tag does not support IsoDep")
            } catch (e: IOException) {
                diagnostics.emitSafely(
                    EgkDiagnosticEvent(
                        operation = EgkDiagnosticOperation.NFC_CONNECT,
                        status = EgkDiagnosticStatus.FAILED,
                        durationMillis = elapsedSince(startNanos),
                        isSecureChannel = false,
                        failureCategory = failureCategoryOf(e),
                        failureType = e::class.simpleName,
                    ),
                )
                throw e
            }

            return try {
                isoDep.apply {
                    Napier.d("Try isoDep connect ...")
                    connect()
                    Napier.d("... isoDep connected")
                    Napier.d("isoDep maxTransceiveLength: $maxTransceiveLength")
                    Napier.d("isoDep timeout: $timeout")
                    timeout = timeoutMillis
                    Napier.d("isoDep timeout set to: $timeout")
                }

                val healthCard = NfcHealthCard(isoDep, diagnostics)

                diagnostics.emitSafely(
                    EgkDiagnosticEvent(
                        operation = EgkDiagnosticOperation.NFC_CONNECT,
                        status = EgkDiagnosticStatus.SUCCEEDED,
                        durationMillis = elapsedSince(startNanos),
                        isSecureChannel = false,
                    ),
                )

                NfcCardChannel(
                    isoDep.isExtendedLengthApduSupported,
                    healthCard,
                    diagnostics,
                )
            } catch (e: IOException) {
                diagnostics.emitSafely(
                    EgkDiagnosticEvent(
                        operation = EgkDiagnosticOperation.NFC_CONNECT,
                        status = EgkDiagnosticStatus.FAILED,
                        durationMillis = elapsedSince(startNanos),
                        isSecureChannel = false,
                        failureCategory = failureCategoryOf(e),
                        failureType = e::class.simpleName,
                    ),
                )
                throw NfcChannelConnectException("Failed to connect to NFC tag", e)
            }
        }

        private fun failureCategoryOf(error: IOException): EgkFailureCategory =
            when (error) {
                is NfcChannelConnectException ->
                    if (error.cause == null) {
                        EgkFailureCategory.UNSUPPORTED_CARD
                    } else {
                        EgkFailureCategory.TRANSPORT_IO
                    }
                is NfcChannelClosedException -> EgkFailureCategory.CHANNEL_CLOSED
                is TagLostException -> EgkFailureCategory.DISCONNECTED
                is InterruptedIOException -> EgkFailureCategory.TIMEOUT
                else -> EgkFailureCategory.TRANSPORT_IO
            }
    }
}
