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

package de.link4health.egk.identifier

/**
 * It is possible that the attribute type shortFileIdentifier is used by the file object types.
 * Short file identifiers are used  for implicit file selection in the immediate context of a command.
 * The value of shortFileIdentifier MUST be an integer in the interval [1, 30]
 *
 * @see "ISO/IEC7816-4 und gemSpec_COS 'Spezifikation des Card Operating System'"
 */
class ShortFileIdentifier(val sfId: Int) {

    companion object {
        private const val MIN_VALUE = 1
        private const val MAX_VALUE = 30
    }

    init {
        sanityCheck()
    }

    /**
     * This class represents a Short File Identifier (SFID), which is used for implicit file selection in the immediate context of a command.
     * The SFID value must be an integer in the range of 1 to 30.
     *
     * @see "ISO*/
    constructor(hexSfId: String) : this(
        hexSfId.toUByte(16).toInt(),
    ) // Converts hexadecimal string to UByte and then to Int

    private fun sanityCheck() {
        require(!(sfId < MIN_VALUE || sfId > MAX_VALUE)) {
            // gemSpec_COS#N007.000
            "Short File Identifier out of valid range [$MIN_VALUE,$MAX_VALUE]"
        }
    }
}
