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

package de.link4health.egk.tagobjects

import de.link4health.egk.Bytes
import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.DERTaggedObject
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.macs.CMac
import org.bouncycastle.crypto.params.KeyParameter
import java.io.ByteArrayOutputStream

/**
 * Mac object with TAG 8E (cryptographic checksum)
 *
 *
 * @param header byte array with extracted header from plain CommandApdu
 * @param commandOutput ByteArrayOutputStream with extracted data and expected length from plain CommandApdu
 * @param kMac byte array with Session key for message authentication
 * @param ssc byte array with send sequence counter
 */
class MacObject(
    private val header: ByteArray? = null,
    private val commandOutput: ByteArrayOutputStream,
    private val kMac: ByteArray,
    private val ssc: ByteArray,
) {
    companion object {
        private const val DO_8E_TAG = 0x0E
        private const val MAC_SIZE = 8
        private const val BLOCK_SIZE = 16
    }

    private var _mac: ByteArray = ByteArray(BLOCK_SIZE)
    val mac: ByteArray
        get() = _mac.copyOf()

    /**
     * Represents a tagged object used for MAC calculation.
     *
     * @property DO_8E_TAG The tag value for the tagged object.
     * @property MAC_SIZE The size of the MAC.
     * @property BLOCK_SIZE The block size for padding.
     */
    val taggedObject: DERTaggedObject
        get() =
            DERTaggedObject(false, DO_8E_TAG, DEROctetString(_mac))

    init {
        calculateMac()
    }

    private fun calculateMac() {
        val cbcMac = getCMac(ssc, kMac)

        if (header != null) {
            val paddedHeader = Bytes.padData(header, BLOCK_SIZE)
            cbcMac.update(paddedHeader, 0, paddedHeader.size)
        }
        if (commandOutput.size() > 0) {
            val paddedData = Bytes.padData(commandOutput.toByteArray(), BLOCK_SIZE)
            cbcMac.update(paddedData, 0, paddedData.size)
        }
        cbcMac.doFinal(_mac, 0)

        _mac = _mac.copyOfRange(0, MAC_SIZE)
    }

    private fun getCMac(secureMessagingSSC: ByteArray, kMac: ByteArray): CMac =
        CMac(AESEngine.newInstance()).apply {
            init(KeyParameter(kMac))
            update(secureMessagingSSC, 0, secureMessagingSSC.size)
        }
}
