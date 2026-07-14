package de.link4health.egk.command

import org.junit.Assert.assertEquals
import kotlin.test.Test

class ResponseStatusTest {

    @Test
    fun generalAuthenticateStatusMapsSuccessCorrectly() {
        assertEquals(ResponseStatus.SUCCESS, generalAuthenticateStatus[0x9000])
    }

    @Test
    fun pinStatusMapsSuccessCorrectly() {
        assertEquals(ResponseStatus.SUCCESS, pinStatus[0x9000])
    }

    @Test
    fun selectStatusMapsSuccessCorrectly() {
        assertEquals(ResponseStatus.SUCCESS, selectStatus[0x9000])
    }

    @Test
    fun readStatusMapsSuccessCorrectly() {
        assertEquals(ResponseStatus.SUCCESS, readStatus[0x9000])
    }

    @Test
    fun verifySecretStatusMapsSuccessCorrectly() {
        assertEquals(ResponseStatus.SUCCESS, verifySecretStatus[0x9000])
    }

    @Test
    fun generalAuthenticateStatusHasNoDuplicateKeys() {
        val keys = generalAuthenticateStatus.keys
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun pinStatusHasNoDuplicateKeys() {
        val keys = pinStatus.keys
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun selectStatusHasNoDuplicateKeys() {
        val keys = selectStatus.keys
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun allStatusMapsContainSuccessEntry() {
        val allMaps = listOf(
            generalAuthenticateStatus,
            pinStatus,
            manageSecurityEnvironmentStatus,
            psoComputeDigitalSignatureStatus,
            internalResponseMessageStatus,
            readStatus,
            selectStatus,
            verifySecretStatus,
            unlockEgkStatus,
            changeReferenceDataStatus,
            getRandomValuesStatus,
        )
        for (map in allMaps) {
            assertEquals(
                "Every status map should map 0x9000 to SUCCESS",
                ResponseStatus.SUCCESS,
                map[0x9000],
            )
        }
    }
}
