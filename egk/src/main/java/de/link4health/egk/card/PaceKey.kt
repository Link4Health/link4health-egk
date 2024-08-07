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
 * Pace Key for TrustedChannel with Session key for encoding and Session key for message authentication
 */
data class PaceKey(val enc: ByteArray, val mac: ByteArray) {
    /**
     * Checks whether the specified object is equal to this PaceKey.
     *
     * The equality check is performed by comparing the content of the 'enc' and 'mac' properties.
     *
     * @param other the object to compare with this PaceKey
     * @return true if the specified object is equal to this PaceKey, false otherwise
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaceKey

        if (!enc.contentEquals(other.enc)) return false
        if (!mac.contentEquals(other.mac)) return false

        return true
    }

    /**
     * Calculates the hash code for the PaceKey object.
     *
     * The hash code is calculated by combining the hash codes of the 'enc' and 'mac' properties using a formula.
     *
     * @return the calculated hash code
     */
    override fun hashCode(): Int {
        var result = enc.contentHashCode()
        result = 31 * result + mac.contentHashCode()
        return result
    }
}
