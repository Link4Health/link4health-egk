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
