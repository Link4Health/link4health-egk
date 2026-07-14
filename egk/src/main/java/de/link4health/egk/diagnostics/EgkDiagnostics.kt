package de.link4health.egk.diagnostics

/**
 * Optional listener for structured, sanitized diagnostics events emitted by the library.
 */
fun interface EgkDiagnostics {
    fun onEvent(event: EgkDiagnosticEvent)

    companion object {
        val NONE: EgkDiagnostics = EgkDiagnostics { }
    }
}
