package de.link4health.egk.nfc

import java.io.IOException

/**
 * Thrown when a caller attempts to use a closed or disconnected NFC channel.
 */
class NfcChannelClosedException(
    message: String = "NFC card channel is closed",
    cause: Throwable? = null,
) : IOException(message, cause)

/**
 * Thrown when establishing a new NFC channel fails.
 */
class NfcChannelConnectException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause)

/**
 * Thrown when closing an NFC channel fails.
 */
class NfcChannelCloseException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause)
