package de.link4health.egk.command

/**
 * APDU Response
 */
class ResponseApdu(apdu: ByteArray) {
    init {
        require(apdu.size >= 2) { "Response APDU must not have less than 2 bytes (status bytes SW1, SW2)" }
    }

    /**
     * Represents an Application Protocol Data Unit (APDU).
     * The APDU is a command-response communication protocol used in smart cards and other devices.
     *
     * @property apdu The byte array representing the APDU.
     */
    val apdu = apdu.copyOf()

    /**
     * Represents the number of bytes in the APDU response.
     *
     * This variable is calculated by subtracting 2 from the size of the APDU.
     * The APDU response is the response received from an Application Protocol Data Unit (APDU) command.
     * The APDU is a command-response communication protocol used in smart cards and other devices.
     *
     * @see ResponseApdu
     *
     * @since 1.0.0
     */
    val nr: Int
        get() = apdu.size - 2

    /**
     * Represents a data byte array obtained from an APDU response.
     *
     * The data represents a subset of the APDU bytes, excluding the last two bytes (status bytes SW1, SW2).
     *
     * @property data The byte array representing the data obtained from the APDU response.
     * @see ResponseApdu
     */
    val data: ByteArray
        get() = apdu.copyOfRange(0, apdu.size - 2)

    /**
     * This variable represents the `sw1` byte of the APDU.
     *
     * The APDU is an application protocol data unit used in smart card communication.
     * It consists of a command APDU sent from the reader to the card and a response APDU sent from the
     * card to the reader.
     *
     * The `sw1` byte indicates the status of the response APDU. It is the second last byte of the response APDU.
     *
     * To access the value of `sw1`, use the `sw1` property. It is retrieved by taking the second last byte (`apdu[apdu.size - 2]`)
     * from the `apdu` array and converting it to an integer using the `toInt()` method. The resulting value is then
     * bitwise ANDed with `0xFF` to get the least significant byte.
     *
     * @see ResponseApdu
     */
    val sw1: Int
        get() = apdu[apdu.size - 2].toInt() and 0xFF

    /**
     * Represents the second status word (SW2) of the response Application Protocol Data Unit (APDU).
     *
     * SW2 is a 1-byte value obtained from the last element of the `apdu` array, converted to an integer and masked with 0xFF.
     *
     * @see ResponseApdu
     */
    val sw2: Int
        get() = apdu[apdu.size - 1].toInt() and 0xFF

    /**
     * Represents the status word of a response from a smart card.
     *
     * The status word consists of two bytes - sw1 and sw2, which are combined to form the final status value.
     *
     * @property sw1 the first byte of the status word
     * @property sw2 the second byte of the status word
     */
    val sw: Int
        get() = sw1 shl 8 or sw2

    /**
     * Represents a byte array.
     * The bytes are obtained by making a copy of the `apdu` field in the parent class `ResponseApdu`.
     *
     * @property bytes The byte array representing the data.
     */
    val bytes: ByteArray
        get() = apdu.copyOf()

    /**
     * Compares this ResponseApdu object to the specified object for equality.
     *
     * @param other The object to compare for equality.
     * @return true if the specified object is equal to this ResponseApdu object, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponseApdu

        if (!apdu.contentEquals(other.apdu)) return false

        return true
    }

    /**
     * Calculates the hash code for the ResponseApdu object.
     *
     * @return The hash code value for the ResponseApdu object.
     */
    override fun hashCode(): Int {
        return apdu.contentHashCode()
    }

    /**
     * Returns a hexadecimal representation of the bytes.
     *
     * Converts the bytes to a hexadecimal string, where each byte is represented by two
     * hexadecimal characters. The bytes are joined together to form the final string.
     *
     * @return The hexadecimal representation of the bytes.
     */
    fun bytesInHex(): String {
        val hexString = bytes.joinToString(separator = "") { byte ->
            String.format("%02X", byte)
        }
        return hexString
    }

    /**
     * Returns a hexadecimal representation of the bytes.
     *
     * Converts the bytes to a hexadecimal string, where each byte is represented by two hexadecimal characters.
     * The bytes are joined together to form the final string.
     *
     * @return The hexadecimal representation of the bytes.
     */
    fun dataInBytesInHex(): String {
        val hexString = data.joinToString(separator = "") { byte ->
            String.format("%02X", byte)
        }
        return hexString
    }
}
