package de.link4health.egk.command

import de.link4health.egk.card.ICardChannel

/**
 * Executes the given [HealthCardCommand] on the specified [ICardChannel] and checks if the response status is successful.
 *
 * @param channel the smart card channel to execute the command on
 * @return a [HealthCardResponse] object representing the response from the smart card
 * @throws ResponseException if the response status is not successful
 */
fun HealthCardCommand.executeSuccessfulOn(channel: ICardChannel): HealthCardResponse =
    this.executeOn(channel).also {
        it.requireSuccess()
    }
