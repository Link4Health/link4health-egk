package de.link4health.egk.card

import java.io.IOException

/**
 * Thrown when a secure channel is reused after a terminal session failure.
 */
class SecureChannelTerminatedException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause)
