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
 * An application identifier (AID) is used to address an application on the card
 */
class ApplicationIdentifier(val aid: ByteArray) {
    companion object {
        private const val AID_MIN_LENGTH = 5
        private const val AID_MAX_LENGTH = 16
    }

    init {
        require(!(aid.size < AID_MIN_LENGTH || aid.size > AID_MAX_LENGTH)) {
            // gemSpec_COS#N010.200
            "Application File Identifier length out of valid range [$AID_MIN_LENGTH,$AID_MAX_LENGTH]"
        }
    }

    /**
     * Constructor for the ApplicationIdentifier class.
     *
     * @param hexAid the hexadecimal representation of the Application Identifier (AID)
     * @throws IllegalArgumentException if the length of the hexAid is invalid
     */
    constructor(hexAid: String) :
        this(hexAid.chunked(2).map { it.toUByte(16).toByte() }.toByteArray())

    /**
     * Represents the value of an Application Identifier (AID).
     *
     * An Application Identifier (AID) is used to address an application on the card.
     *
     * @property aidValue The value of the Application Identifier (AID) represented as a byte array.
     */
    val aidValue: ByteArray get() = aid.copyOf()
}
