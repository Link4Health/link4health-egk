package de.link4health.egk.tagobjects

import de.link4health.egk.command.EXPECTED_LENGTH_WILDCARD_SHORT
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import kotlin.test.Test

class LengthObjectTest {

    @Test
    fun shortLengthEncoding() {
        val lo = LengthObject(127)
        assertNotNull(lo.taggedObject)
    }

    @Test
    fun wildcardShortEncoding() {
        val lo = LengthObject(EXPECTED_LENGTH_WILDCARD_SHORT)
        assertNotNull(lo.taggedObject)
    }

    @Test
    fun extendedLengthEncoding() {
        val lo = LengthObject(257)
        assertNotNull(lo.taggedObject)
    }

    @Test
    fun negativeLeLeavesEmptyData() {
        val lo = LengthObject(-1)
        assertNotNull(lo.taggedObject)
    }

    @Test
    fun instancesAreIndependentRegressionTest() {
        val lo1 = LengthObject(127)
        val encoded1 = lo1.taggedObject.encoded

        val lo2 = LengthObject(257)

        // lo1 should still produce the same encoding after lo2 was created
        val encodedAfter = lo1.taggedObject.encoded
        assertFalse(
            "LengthObject instances must not share state",
            lo2.taggedObject.encoded.contentEquals(encodedAfter),
        )
        assertTrue(
            "LengthObject encoding should not change after another instance is created",
            encoded1.contentEquals(encodedAfter),
        )
    }
}
