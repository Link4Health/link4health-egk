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

package de.link4health.egk.card

/**
 * Class applies for symmetric keys and private keys.
 */
class CardKey(private val keyId: Int) : ICardKeyReference {

    companion object {
        private const val MIN_KEY_ID = 2
        private const val MAX_KEY_ID = 28
    }

    init {
        require(!(keyId < MIN_KEY_ID || keyId > MAX_KEY_ID)) {
            // gemSpec_COS#N016.400 and #N017.100
            "Key ID out of range [$MIN_KEY_ID,$MAX_KEY_ID]"
        }
    }

    /**
     * Calculates the key reference based on the given parameters.
     *
     * @param dfSpecific indicates whether the key is DF specific
     * @return the calculated key reference
     */
    override fun calculateKeyReference(dfSpecific: Boolean): Int {
        // gemSpec_COS#N099.600
        var keyReference = keyId
        if (dfSpecific) {
            keyReference += ICardKeyReference.DF_SPECIFIC_PWD_MARKER
        }
        return keyReference
    }
}
