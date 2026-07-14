package de.link4health.egk.diagnostics

/**
 * Sanitized high-level failure categories for eGK operations.
 */
enum class EgkFailureCategory {
    TRANSPORT_IO,
    TIMEOUT,
    DISCONNECTED,
    CHANNEL_CLOSED,
    MALFORMED_APDU,
    SECURE_MESSAGING,
    PACE_NEGOTIATION,
    UNSUPPORTED_CARD,
    PROTOCOL_STATE,
    UNKNOWN,
}
