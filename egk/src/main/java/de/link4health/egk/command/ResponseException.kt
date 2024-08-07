package de.link4health.egk.command

/**
 * Exception thrown when a response status is encountered that indicates an error or unexpected behavior.
 *
 * @property responseStatus The response status that caused the exception.
 */
class ResponseException(val responseStatus: ResponseStatus) : Exception()
