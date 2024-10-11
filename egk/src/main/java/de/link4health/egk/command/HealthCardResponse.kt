package de.link4health.egk.command

/**
 * Represents the response from a health card.
 *
 * @property status The status of the response.
 * @property apdu The response APDU.
 *
 * @see [ResponseStatus]
 */
class HealthCardResponse(
    val status: ResponseStatus,
    val apdu: ResponseApdu,
)
