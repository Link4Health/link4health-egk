package de.link4health.egk.command

import java.io.ByteArrayOutputStream

/**
 * Value for when wildcardShort for expected length encoding is needed
 */
const val EXPECTED_LENGTH_WILDCARD_EXTENDED: Int = 65536
const val EXPECTED_LENGTH_WILDCARD_SHORT: Int = 256
const val BYTE_MASK: Int = 0xFF
const val BYTE_SHIFT: Int = 8
const val MAX_APDU_HEADER_VALUE: Int = 255
const val MAX_APDU_DATA_LENGTH: Int = 65535
const val DATA_OFFSET_SHORT: Int = 5
const val DATA_OFFSET_EXTENDED: Int = 7

private fun encodeDataLengthExtended(nc: Int): ByteArray =
    byteArrayOf(0x0, (nc shr BYTE_SHIFT).toByte(), (nc and BYTE_MASK).toByte())

private fun encodeDataLengthShort(nc: Int): ByteArray =
    byteArrayOf(nc.toByte())

private fun encodeExpectedLengthExtended(ne: Int): ByteArray =
    if (ne != EXPECTED_LENGTH_WILDCARD_EXTENDED) {
        byteArrayOf((ne shr BYTE_SHIFT).toByte(), (ne and BYTE_MASK).toByte())
    } else {
        byteArrayOf(0x0, 0x0)
    }

private fun encodeExpectedLengthShort(ne: Int): ByteArray =
    byteArrayOf(
        if (ne != EXPECTED_LENGTH_WILDCARD_EXTENDED) {
            ne.toByte()
        } else {
            0x0
        },
    )

/**
 * An APDU (Application Protocol Data Unit) Command per ISO/IEC 7816-4.
 *
 * Command APDU encoding options:
 *
 * ```
 * case 1:  |CLA|INS|P1 |P2 |                                 len = 4
 * case 2s: |CLA|INS|P1 |P2 |LE |                             len = 5
 * case 3s: |CLA|INS|P1 |P2 |LC |...BODY...|                  len = 6..260
 * case 4s: |CLA|INS|P1 |P2 |LC |...BODY...|LE |              len = 7..261
 * case 2e: |CLA|INS|P1 |P2 |00 |LE1|LE2|                     len = 7
 * case 3e: |CLA|INS|P1 |P2 |00 |LC1|LC2|...BODY...|          len = 8..65542
 * case 4e: |CLA|INS|P1 |P2 |00 |LC1|LC2|...BODY...|LE1|LE2|  len =10..65544
 *
 * LE, LE1, LE2 may be 0x00.
 * LC must not be 0x00 and LC1|LC2 must not be 0x00|0x00
 * ```
 */
class CommandApdu(
    apduBytes: ByteArray,
    val rawNc: Int,
    val rawNe: Int?,
    val dataOffset: Int,
) {
    private val _apduBytes = apduBytes.copyOf()

    /**
     * Returns a copy of the bytes stored in the `_apduBytes` property.
     */
    val bytes get() = _apduBytes.copyOf()

    /**
     * Converts the bytes in the `bytes` list to a hexadecimal string representation.
     *
     * @return The hexadecimal representation of the bytes.
     */
    fun apduInHex(): String {
        val hexString = bytes.joinToString(separator = "") { byte ->
            String.format("%02X", byte)
        }
        return hexString
    }

    companion object {
        /**
         * Creates a CommandApdu object with the provided options.
         *
         * @param cla The class byte.
         * @param ins The instruction byte.
         * @param p1 The P1 parameter byte.
         * @param p2 The P2 parameter byte.
         * @param ne The expected response length. Can be null if no expected length is specified.
         * @return The CommandApdu object with the specified options.
         *
         * @throws IllegalArgumentException if any of the header fields (cla, ins, p1, p2) are less than 0 or greater than 255,
         * or if the APDU response length is out of bounds [0, 65536].
         */
        fun ofOptions(
            cla: Int,
            ins: Int,
            p1: Int,
            p2: Int,
            ne: Int?,
        ) = ofOptions(cla = cla, ins = ins, p1 = p1, p2 = p2, data = null, ne = ne)

        /**
         * Creates a CommandApdu object with the provided options.
         *
         * @param cla The class byte.
         * @param ins The instruction byte.
         * @param p1 The P1 parameter byte.
         * @param p2 The P2 parameter byte.
         * @param data The command data as a ByteArray. Can be null if no data is specified.
         * @param ne The expected response length. Can be null if no expected length is specified.
         * @return The CommandApdu object with the specified options.
         *
         * @throws IllegalArgumentException if any of the header fields (cla, ins, p1, p2) are less than 0 or greater than 255,
         * or if the APDU response length is out of bounds [0, 65536].
         */
        fun ofOptions(
            cla: Int,
            ins: Int,
            p1: Int,
            p2: Int,
            data: ByteArray?,
            ne: Int?,
        ): CommandApdu {
            require(!(cla < 0 || ins < 0 || p1 < 0 || p2 < 0)) {
                "APDU header fields must not be less than 0"
            }
            require(!(cla > MAX_APDU_HEADER_VALUE || ins > MAX_APDU_HEADER_VALUE || p1 > MAX_APDU_HEADER_VALUE || p2 > MAX_APDU_HEADER_VALUE)) {
                "APDU header fields must not be greater than $MAX_APDU_HEADER_VALUE (0xFF)"
            }
            ne?.let {
                require(ne <= EXPECTED_LENGTH_WILDCARD_EXTENDED || ne >= 0) {
                    "APDU response length is out of bounds [0, $EXPECTED_LENGTH_WILDCARD_EXTENDED]"
                }
            }

            val bytes = ByteArrayOutputStream()
            // write header |CLA|INS|P1 |P2 |
            bytes.write(byteArrayOf(cla.toByte(), ins.toByte(), p1.toByte(), p2.toByte()))

            return if (data != null) {
                val nc = data.size
                require(nc <= MAX_APDU_DATA_LENGTH) { "APDU cmd data length must not exceed $MAX_APDU_DATA_LENGTH bytes" }

                val dataOffset: Int
                val le: Int? // le1, le2
                if (ne != null) {
                    le = ne
                    // case 4s or 4e
                    if (nc <= MAX_APDU_HEADER_VALUE && ne <= EXPECTED_LENGTH_WILDCARD_SHORT) {
                        // case 4s
                        dataOffset = DATA_OFFSET_SHORT
                        bytes.write(encodeDataLengthShort(nc))
                        bytes.write(data)
                        bytes.write(encodeExpectedLengthShort(ne))
                    } else {
                        // case 4e
                        dataOffset = DATA_OFFSET_EXTENDED
                        bytes.write(encodeDataLengthExtended(nc))
                        bytes.write(data)
                        bytes.write(encodeExpectedLengthExtended(ne))
                    }
                } else {
                    // case 3s or 3e
                    le = null
                    if (nc <= MAX_APDU_HEADER_VALUE) {
                        // case 3s
                        dataOffset = DATA_OFFSET_SHORT
                        bytes.write(encodeDataLengthShort(nc))
                    } else {
                        // case 3e
                        dataOffset = DATA_OFFSET_EXTENDED
                        bytes.write(encodeDataLengthExtended(nc))
                    }
                    bytes.write(data)
                }

                CommandApdu(
                    apduBytes = bytes.toByteArray(),
                    rawNc = nc,
                    rawNe = le,
                    dataOffset = dataOffset,
                )
            } else {
                // data empty
                if (ne != null) {
                    // case 2s or 2e
                    if (ne <= EXPECTED_LENGTH_WILDCARD_SHORT) {
                        // case 2s
                        // 256 is encoded 0x0
                        bytes.write(encodeExpectedLengthShort(ne))
                    } else {
                        // case 2e
                        bytes.write(0x0)
                        bytes.write(encodeExpectedLengthExtended(ne))
                    }

                    CommandApdu(
                        apduBytes = bytes.toByteArray(),
                        rawNc = 0,
                        rawNe = ne,
                        dataOffset = 0,
                    )
                } else {
                    // case 1
                    CommandApdu(
                        apduBytes = bytes.toByteArray(),
                        rawNc = 0,
                        rawNe = null,
                        dataOffset = 0,
                    )
                }
            }
        }
    }
}
