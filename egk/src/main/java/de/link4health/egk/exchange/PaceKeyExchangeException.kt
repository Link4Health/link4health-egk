package de.link4health.egk.exchange

import java.io.IOException

/**
 * Thrown when the PACE key negotiation cannot be completed successfully.
 */
open class PaceKeyExchangeException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause)

/**
 * Thrown when PACE mutual authentication fails because the MAC derived on the device
 * does not match the MAC returned by the card. In practice this almost always means
 * the provided CAN (Card Access Number) is wrong, so callers can prompt the user to
 * re-enter the CAN instead of treating it as a transport error.
 */
class PaceMacMismatchException(
    message: String,
    cause: Throwable? = null,
) : PaceKeyExchangeException(message, cause)

/**
 * Thrown when the detected eGK card is unsupported for the requested operation.
 */
class UnsupportedEgkCardException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause)
