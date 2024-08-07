package de.link4health.egk.command


/**
 * Checks if the response status of the `HealthCardResponse` object is `SUCCESS`. If it is not,
 * throws a `ResponseException`.
 *
 * @throws ResponseException if the response status is not `SUCCESS`.
 */
fun HealthCardResponse.requireSuccess() {
    if (this.status != ResponseStatus.SUCCESS) {
        throw ResponseException(this.status)
    }
}
