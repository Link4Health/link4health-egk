package de.link4health.egk.exchange

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import kotlin.test.Test

class KeyDerivationFunctionTest {

    private val sharedSecret = ByteArray(32) { it.toByte() }

    @Test
    fun getAES128KeyReturns16Bytes() {
        val key = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.ENC)
        assertEquals(16, key.size)
    }

    @Test
    fun encAndMacProduceDifferentKeys() {
        val encKey = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.ENC)
        val macKey = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.MAC)
        assertFalse(encKey.contentEquals(macKey))
    }

    @Test
    fun encAndPasswordProduceDifferentKeys() {
        val encKey = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.ENC)
        val pwKey = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.PASSWORD)
        assertFalse(encKey.contentEquals(pwKey))
    }

    @Test
    fun macAndPasswordProduceDifferentKeys() {
        val macKey = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.MAC)
        val pwKey = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.PASSWORD)
        assertFalse(macKey.contentEquals(pwKey))
    }

    @Test
    fun sameInputProducesDeterministicOutput() {
        val key1 = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.ENC)
        val key2 = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.ENC)
        assertEquals(true, key1.contentEquals(key2))
    }

    @Test
    fun differentInputProducesDifferentKeys() {
        val secret2 = ByteArray(32) { (it + 1).toByte() }
        val key1 = KeyDerivationFunction.getAES128Key(sharedSecret, KeyDerivationFunction.Mode.ENC)
        val key2 = KeyDerivationFunction.getAES128Key(secret2, KeyDerivationFunction.Mode.ENC)
        assertFalse(key1.contentEquals(key2))
    }
}
