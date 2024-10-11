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
 * interface that identifier:
 *
 * - symmetric authentication object,
 * - symmetric map connection object,
 * - or private key object
 */
interface ICardKeyReference {
    /**
     * Calculates the key reference based on the specified flag.
     *
     * @param dfSpecific flag indicating whether the calculation is specific to the identifier.
     *                   If true, the identifier is a symmetric authentication object,
     *                   symmetric map connection object, or private key object.
     *                   If false, the identifier is not specific to any particular type.
     * @return the calculated key reference.
     */
    fun calculateKeyReference(dfSpecific: Boolean): Int

    companion object {
        const val DF_SPECIFIC_PWD_MARKER = 0x80
    }
}
