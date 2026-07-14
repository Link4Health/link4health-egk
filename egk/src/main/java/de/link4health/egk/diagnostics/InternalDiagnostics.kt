package de.link4health.egk.diagnostics

import android.nfc.TagLostException
import de.link4health.egk.card.ICardChannel
import de.link4health.egk.card.MalformedSecureMessagingApduException
import de.link4health.egk.card.SecureChannelTerminatedException
import de.link4health.egk.card.SecureMessagingException
import de.link4health.egk.exchange.PaceKeyExchangeException
import de.link4health.egk.exchange.UnsupportedEgkCardException
import de.link4health.egk.nfc.NfcCardChannel
import de.link4health.egk.nfc.NfcCardSecureChannel
import de.link4health.egk.nfc.NfcChannelClosedException
import java.io.IOException
import java.io.InterruptedIOException

internal fun ICardChannel.diagnosticsOrNone(): EgkDiagnostics =
    when (this) {
        is NfcCardChannel -> diagnostics
        is NfcCardSecureChannel -> diagnostics
        else -> EgkDiagnostics.NONE
    }

internal fun EgkDiagnostics.emitSafely(event: EgkDiagnosticEvent) {
    if (this === EgkDiagnostics.NONE) return
    try {
        onEvent(event)
    } catch (_: Exception) {
        // Diagnostics must never alter library behavior.
    }
}

internal fun elapsedSince(startNanos: Long): Long =
    (System.nanoTime() - startNanos) / 1_000_000

internal fun Throwable.toFailureCategory(): EgkFailureCategory =
    when (this) {
        is UnsupportedEgkCardException -> EgkFailureCategory.UNSUPPORTED_CARD
        is PaceKeyExchangeException -> EgkFailureCategory.PACE_NEGOTIATION
        is NfcChannelClosedException -> EgkFailureCategory.CHANNEL_CLOSED
        is TagLostException -> EgkFailureCategory.DISCONNECTED
        is InterruptedIOException -> EgkFailureCategory.TIMEOUT
        is MalformedSecureMessagingApduException -> EgkFailureCategory.MALFORMED_APDU
        is SecureMessagingException -> EgkFailureCategory.SECURE_MESSAGING
        is SecureChannelTerminatedException -> EgkFailureCategory.PROTOCOL_STATE
        is IOException -> EgkFailureCategory.TRANSPORT_IO
        is IllegalArgumentException -> EgkFailureCategory.MALFORMED_APDU
        else -> EgkFailureCategory.UNKNOWN
    }
