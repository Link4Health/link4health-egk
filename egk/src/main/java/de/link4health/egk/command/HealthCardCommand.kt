/*
 * Copyright (c) 2024 gematik GmbH
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the Licence);
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *     https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package de.link4health.egk.command

import de.link4health.egk.card.ICardChannel


/**
 * Superclass for all HealthCardCommands
 */
class HealthCardCommand(
    val expectedStatus: Map<Int, ResponseStatus>,
    val cla: Int,
    val ins: Int,
    val p1: Int = 0,
    val p2: Int = 0,
    val data: ByteArray? = null,
    val ne: Int? = null
) {

    companion object {
        private const val HEX_FF = 0xff

        const val NE_MAX_EXTENDED_LENGTH = 65536
        const val NE_MAX_SHORT_LENGTH = 256
        const val EXPECT_ALL_WILDCARD = -1
    }

    init {
        require(!(cla > HEX_FF || ins > HEX_FF || p1 > HEX_FF || p2 > HEX_FF)) { "Parameter value exceeds one byte" }
    }

    /**
     * Executes the given [HealthCardCommand] on the specified [ICardChannel].
     *
     * @param channel the smart card channel to execute the command on
     * @return a [HealthCardResponse] object representing the response from the smart card
     */
    fun executeOn(channel: ICardChannel): HealthCardResponse {
        val cApdu = getCommandApdu(channel)
        return channel.transmit(cApdu).let {
            HealthCardResponse(expectedStatus[it.sw] ?: ResponseStatus.UNKNOWN_STATUS, it)
        }
    }

    private fun getCommandApdu(channel: ICardChannel): CommandApdu {
        val expectedLength = if (ne != null && ne == EXPECT_ALL_WILDCARD) {
            if (channel.isExtendedLengthSupported) {
                NE_MAX_EXTENDED_LENGTH
            } else {
                NE_MAX_SHORT_LENGTH
            }
        } else {
            ne
        }

        val commandAPDU = CommandApdu.ofOptions(cla, ins, p1, p2, data, expectedLength)
        val apduLength = commandAPDU.bytes.size
        require(apduLength <= channel.maxTransceiveLength) {
            "CommandApdu is too long to send. Limit for Reader is " + channel.maxTransceiveLength +
                    " but length of commandApdu is " + apduLength
        }
        return commandAPDU
    }
}
