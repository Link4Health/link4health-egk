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
 * A file identifier may reference any file. It consists of two bytes. The value '3F00'
 * is reserved for referencing the MF. The value 'FFFF' is reserved for future use. The value '3FFF' is reserved
 * (see below and 7.4.1). The value '0000' is reserved (see 7.2.2 and 7.4.1). In order to unambiguously select
 * any file by its identifier, all EFs and DFs immediately under a given DF shall have different file identifiers.
 * @see "ISO/IEC 7816-4"
 */
class FileIdentifier {
    private val fid: Int

    /**
     * Constructs a `FileIdentifier` object from a given byte array.
     *
     * @param fid the byte array containing the file identifier value (2 bytes)
     * @throws IllegalArgumentException if the length of `fid` is not 2
     */
    constructor(fid: ByteArray) {
        require(fid.size == 2) { "requested length of byte array for a File Identifier value is 2 but was " + fid.size }
        this.fid = fid.fold(0) { accum, value -> (accum shl 8) + value.toInt() }
        sanityCheck()
    }

    /**
     * Constructs a FileIdentifier object with the given file identifier.
     *
     * @param fid the file identifier
     */
    constructor(fid: Int) {
        this.fid = fid
        sanityCheck()
    }

    /**
     * A FileIdentifier represents a file identifier in a smart card system. It consists of two bytes which uniquely identify
     * a file within the system. This class provides various constructors to create a FileIdentifier object from different input types.
     *
     * @param fid the file identifier value
     * @throws IllegalArgumentException if the file identifier is out of range
     */
    constructor(fid: String) : this(fid.toInt(16))

    /**
     * Retrieves the File Identifier (FID) as a byte array.
     *
     * @return The File Identifier (FID) as a byte array.
     */
    fun getFid(): ByteArray {
        return byteArrayOf((fid shr 8).toByte(), fid.toByte())
    }

    private fun sanityCheck() {
        // gemSpec_COS#N006.700, N006.900
        require(!((fid < 0x1000 || fid > 0xFEFF) && fid != 0x011C || fid == 0x3FFF)) {
            "File Identifier is out of range: 0x" + fid.toString(16).padStart(4, '0')
        }
    }
}
