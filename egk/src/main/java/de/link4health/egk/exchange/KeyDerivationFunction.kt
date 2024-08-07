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

package de.link4health.egk.exchange

import org.bouncycastle.crypto.digests.SHA1Digest


/**
 * This class provides functionality to derive AES-128 keys.
 */
object KeyDerivationFunction {

    private const val CHECKSUM_LENGTH = 20
    private const val AES128LENGTH = 16
    private const val OFFSET_LENGTH = 4
    private const val ENC_LAST_BYTE = 1
    private const val MAC_LAST_BYTE = 2
    private const val PASSWORD_LAST_BYTE = 3

    /**
     * derive AES-128 key
     *
     * @param sharedSecretK byte array with shared secret value.
     * @param mode key derivation for ENC, MAC or derivation from password
     * @return byte array with AES-128 key
     */
    fun getAES128Key(sharedSecretK: ByteArray, mode: Mode): ByteArray {
        val checksum = ByteArray(CHECKSUM_LENGTH)
        val data = replaceLastKeyByte(sharedSecretK, mode)
        SHA1Digest().apply {
            update(data, 0, data.size)
            doFinal(checksum, 0)
        }
        return checksum.copyOf(AES128LENGTH)
    }

    private fun replaceLastKeyByte(key: ByteArray, mode: Mode): ByteArray =
        ByteArray(key.size + OFFSET_LENGTH).apply {
            key.copyInto(this)
            this[this.size - 1] = when (mode) {
                Mode.ENC -> ENC_LAST_BYTE.toByte()
                Mode.MAC -> MAC_LAST_BYTE.toByte()
                Mode.PASSWORD -> PASSWORD_LAST_BYTE.toByte()
            }
        }

    /**
     * Represents the mode for key derivation.
     */
    enum class Mode {
        ENC, // key for encryption/decryption
        MAC, // key for MAC
        PASSWORD // encryption keys from a password
    }
}
