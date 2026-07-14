package de.link4health.egk.diagnostics

/**
 * Lifecycle stage for a structured diagnostics event.
 */
enum class EgkDiagnosticStatus {
    STARTED,
    SUCCEEDED,
    FAILED,
}

/**
 * Operation types emitted by the library diagnostics hook.
 */
enum class EgkDiagnosticOperation {
    NFC_CONNECT,
    APDU_TRANSMIT,
    SECURE_CHANNEL_TRANSMIT,
    SECURE_MESSAGING_ENCRYPT,
    SECURE_MESSAGING_DECRYPT,
    PACE_NEGOTIATION,
    CHANNEL_CLOSE,
}

/**
 * Sanitized diagnostics payload for NFC and secure-session operations.
 */
data class EgkDiagnosticEvent(
    val operation: EgkDiagnosticOperation,
    val status: EgkDiagnosticStatus,
    val phase: String? = null,
    val durationMillis: Long? = null,
    val commandLength: Int? = null,
    val responseLength: Int? = null,
    val isSecureChannel: Boolean? = null,
    val failureCategory: EgkFailureCategory? = null,
    val failureType: String? = null,
)
