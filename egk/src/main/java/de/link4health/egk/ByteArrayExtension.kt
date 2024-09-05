/*
 *  (C)opyright 2024 eHealth experts GmbH
 *
 *  This code was created by Normen Düring, owned by eHealth experts GmbH.
 *  Any unauthorized use, distribution, reproduction, or disclosure
 *  of this code, or any parts of it, in any form is prohibited.
 *
 *  For any questions, comments or suggestions, please contact normen.duering@ehealthexperts.de
 */
package de.link4health.egk

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Encodes the ByteArray to Base64 string.
 *
 * @return The Base64 encoded string of the ByteArray.
 */
@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.encodeBase64(): String = Base64.encode(this)

/**
 * Decodes a Base64 encoded string into a byte array.
 *
 * @return The decoded byte array.
 */
@OptIn(ExperimentalEncodingApi::class)
fun String.decodeBase64(): ByteArray = Base64.decode(this)
