package de.link4health.egk.command

/**
 * Exception thrown when a response status is encountered that indicates an error or unexpected behavior.
 *
 * @property responseStatus The response status that caused the exception.
 */
class ResponseException(
    val responseStatus: ResponseStatus,
    val statusWord: Int? = null,
) : Exception(
    buildString {
        append("Card response status: ")
        append(responseStatus)
        statusWord?.let { append(" (SW=%04X)".format(it)) }
    },
)
