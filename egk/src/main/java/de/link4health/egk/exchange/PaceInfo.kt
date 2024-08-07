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

import de.link4health.egk.CardUtilities
import org.bouncycastle.asn1.*
import org.bouncycastle.jce.ECNamedCurveTable

private const val PARAMETER256 = 13
private const val PARAMETER384 = 16
private const val PARAMETER512 = 17

/**
 * Extracts PACE Information from CardAccess
 */
class PaceInfo(cardAccess: ByteArray) {
    private val protocol: ASN1ObjectIdentifier
    private val parameterID: Int

    /**
     * Returns PACE info protocol bytes
     */
    val paceInfoProtocolBytes: ByteArray =
        ASN1InputStream(cardAccess).use { asn1InputStream ->
            val app = asn1InputStream.readObject() as DLSet
            val seq = app.getObjectAt(0) as ASN1Sequence
            protocol = seq.getObjectAt(0) as ASN1ObjectIdentifier
            parameterID = (seq.getObjectAt(2) as ASN1Integer).value.toInt()

            protocol.encoded.let {
                it.copyOfRange(2, it.size)
            }
        }

    /**
     * PACE info protocol ID
     */
    val protocolID: String = protocol.id

    private val ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec(
        when (parameterID) {
            PARAMETER256 -> "BrainpoolP256r1"
            PARAMETER384 -> "BrainpoolP384r1"
            PARAMETER512 -> "BrainpoolP512r1"
            else -> ""
        }
    )

    /**
     * Provides the elliptic curve used in the PACE protocol.
     */
    val ecCurve = ecNamedCurveParameterSpec.curve

    /**
     * Represents the EC point G used in the PACE protocol.
     * This variable is extracted from the `ecNamedCurveParameterSpec` property of the `PaceInfo` class.
     * It represents the generator point of the elliptic curve used in the PACE protocol.
     *
     * @property ecPointG The EC point G.
     * @see PaceInfo
     */
    val ecPointG = ecNamedCurveParameterSpec.g

    /**
     * Converts a byte array representation of an ECPoint to an ECPoint object.
     *
     * @param ecPoint The byte array representing the ECPoint. It should be of the form {0x04 || x-bytes [] || y byte []},
     * where the first byte must be 0x04 to indicate an uncompressed point.
     * @return The ECPoint object generated from the input byte array.
     * @throws IllegalArgumentException if the first byte of the input byte array is not 0x04.
     * @see PaceInfo
     */
    fun convertECPoint(ecPoint: ByteArray) =
        CardUtilities.byteArrayToECPoint(ecPoint, ecCurve)
}
