package de.link4health.egk.card

/**
 * Minimum version constant for EGK21.
 *
 * The EGK21_MIN_VERSION constant represents the minimum version required for the EGK21 card.
 * It is defined as the bitwise OR operation of the following values:
 * - ((4 shl 16) or (4 shl 8) or 0).
 * The binary representation of this constant is 00000100 00000100 00000000.
 *
 * @see HealthCardVersion2.isEGK21
 */
const val EGK21_MIN_VERSION = (4 shl 16) or (4 shl 8) or 0

/**
 * Checks if the HealthCardVersion2 object represents an eGK21 card.
 *
 * @return true if the card is an eGK21 card, false otherwise
 */
fun HealthCardVersion2.isEGK21(): Boolean {
    val v = this.objectSystemVersion
    val version = (v[0].toInt() shl 16) or (v[1].toInt() shl 8) or v[1].toInt()

    return version >= EGK21_MIN_VERSION
}
