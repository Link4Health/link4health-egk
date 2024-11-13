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

package de.link4health.egk.nfc

import de.link4health.egk.card.BLOCK_SIZE
import de.link4health.egk.card.ICardChannel
import de.link4health.egk.card.PaceKey
import de.link4health.egk.card.SecureMessaging
import de.link4health.egk.command.CommandApdu
import de.link4health.egk.command.ResponseApdu
import io.github.aakira.napier.Napier

class NfcCardSecureChannel(
    override val isExtendedLengthSupported: Boolean,
    private val nfcHealthCard: NfcHealthCard,
    paceKey: PaceKey,
) : ICardChannel {
    private var secureMessaging = SecureMessaging(paceKey, ByteArray(BLOCK_SIZE))

    override val card: NfcHealthCard get() = nfcHealthCard

    override val maxTransceiveLength = card.isoDep.maxTransceiveLength

    /**
     * Returns the responseApdu after transmitting a commandApdu
     */
    override fun transmit(command: CommandApdu): ResponseApdu {
        Napier.d("Encrypt ----")
        return secureMessaging.encrypt(command).let { encryptedCommand ->
            Napier.d("encrypted ----")
            nfcHealthCard.transmit(encryptedCommand).let { encryptedResponse ->
                Napier.d("Decrypt ----")
                secureMessaging.decrypt(encryptedResponse)
            }
        }
    }
}
