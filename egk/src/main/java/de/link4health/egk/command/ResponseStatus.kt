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

package de.link4health.egk.command

/**
 * Map of general authentication status codes and their corresponding response status.
 *
 * @see ResponseStatus
 */
val generalAuthenticateStatus = mapOf(
    0x0000 to ResponseStatus.UNKNOWN_STATUS,
    0x9000 to ResponseStatus.SUCCESS,
    0x6300 to ResponseStatus.AUTHENTICATION_FAILURE,
    0x6400 to ResponseStatus.PARAMETER_MISMATCH,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6983 to ResponseStatus.KEY_EXPIRED,
    0x6985 to ResponseStatus.NO_KEY_REFERENCE,
    0x6A80 to ResponseStatus.NUMBER_PRECONDITION_WRONG,
    0x6A81 to ResponseStatus.UNSUPPORTED_FUNCTION,
    0x6A88 to ResponseStatus.KEY_NOT_FOUND
)

/**
 * pinStatus is a read-only map that maps response status codes (hexadecimal) to ResponseStatus enum values.
 * It represents the status of a Personal Identification Number (PIN) in a system.
 */
val pinStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x62C1 to ResponseStatus.TRANSPORT_STATUS_TRANSPORT_PIN,
    0x62C7 to ResponseStatus.TRANSPORT_STATUS_EMPTY_PIN,
    0x62D0 to ResponseStatus.PASSWORD_DISABLED,
    0x63C0 to ResponseStatus.RETRY_COUNTER_COUNT_00,
    0x63C1 to ResponseStatus.RETRY_COUNTER_COUNT_01,
    0x63C2 to ResponseStatus.RETRY_COUNTER_COUNT_02,
    0x63C3 to ResponseStatus.RETRY_COUNTER_COUNT_03,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6988 to ResponseStatus.PASSWORD_NOT_FOUND
)

/**
 * Manages the security environment status codes.
 *
 * @property manageSecurityEnvironmentStatus A map that stores the security environment status codes and their corresponding response statuses.
 * @see ResponseStatus
 */
val manageSecurityEnvironmentStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x6A81 to ResponseStatus.UNSUPPORTED_FUNCTION,
    0x6A88 to ResponseStatus.KEY_NOT_FOUND
)

/**
 * Map representing the status codes for the psoComputeDigitalSignature command.
 * The key is the status code, and the value is the corresponding response status.
 * The status codes and response statuses are taken from the gemSpec_COS_16.2 document.
 *
 * @see ResponseStatus
 */
val psoComputeDigitalSignatureStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x6400 to ResponseStatus.KEY_INVALID,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6985 to ResponseStatus.NO_KEY_REFERENCE,
    0x6A81 to ResponseStatus.UNSUPPORTED_FUNCTION,
    0x6A88 to ResponseStatus.KEY_NOT_FOUND
)

/**
 * This variable maps the response status codes to their corresponding ResponseStatus enum values.
 *
 * The response status codes are defined as hexadecimal values, and this map provides a convenient
 * way to look up the corresponding ResponseStatus value for a given response status code.
 *
 * The mapping is as follows:
 * - 0x9000 maps to ResponseStatus.SUCCESS
 * - 0x6400 maps to ResponseStatus.KEY_INVALID
 * - 0x6982 maps to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED
 * - 0x6985 maps to ResponseStatus.NO_KEY_REFERENCE
 * - 0x6A81 maps to ResponseStatus.UNSUPPORTED_FUNCTION
 * - 0x6A80 maps to ResponseStatus.WRONG_TOKEN
 * - 0x6A88 maps to ResponseStatus.KEY_NOT_FOUND
 *
 * @see ResponseStatus
 */
val internalResponseMessageStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x6400 to ResponseStatus.KEY_INVALID,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6985 to ResponseStatus.NO_KEY_REFERENCE,
    0x6A81 to ResponseStatus.UNSUPPORTED_FUNCTION,
    0x6A80 to ResponseStatus.WRONG_TOKEN,
    0x6A88 to ResponseStatus.KEY_NOT_FOUND
)

/**
 * Map of response status codes to their corresponding response status.
 */
val readStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x6281 to ResponseStatus.CORRUPT_DATA_WARNING,
    0x6282 to ResponseStatus.END_OF_FILE_WARNING,
    0x6981 to ResponseStatus.WRONG_FILE_TYPE,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6986 to ResponseStatus.NO_CURRENT_EF,
    0x6A82 to ResponseStatus.FILE_NOT_FOUND,
    0x6B00 to ResponseStatus.OFFSET_TOO_BIG
)

/**
 * Variable that holds a map of select status codes and their corresponding response status.
 *
 * @see ResponseStatus
 */
val selectStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x6283 to ResponseStatus.FILE_DEACTIVATED,
    0x6285 to ResponseStatus.FILE_TERMINATED,
    0x6A82 to ResponseStatus.FILE_NOT_FOUND,
    0x6D00 to ResponseStatus.INSTRUCTION_NOT_SUPPORTED
)

/**
 * This variable represents a map that maps response status codes to their corresponding ResponseStatus enum values.
 *
 * The keys of the map are hexadecimal values representing response status codes, and the values are the corresponding ResponseStatus enum values.
 *
 * The map is used to determine the expected response status for the Verify Secret Command in the HealthCardCommand class.
 *
 * Example usage:
 * val veriftySecretStatus = mapOf(
 *     0x9000 to ResponseStatus.SUCCESS,
 *     0x63C0 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_00,
 *     0x63C1 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_01,
 *     ...
 *     0x6988 to ResponseStatus.PASSWORD_NOT_FOUND
 * )
 */
val verifySecretStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x63C0 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_00,
    0x63C1 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_01,
    0x63C2 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_02,
    0x63C3 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_03,
    0x6581 to ResponseStatus.MEMORY_FAILURE,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6983 to ResponseStatus.PASSWORD_BLOCKED,
    0x6985 to ResponseStatus.PASSWORD_NOT_USABLE,
    0x6988 to ResponseStatus.PASSWORD_NOT_FOUND
)

/**
 * Map of unlock EGK status codes.
 *
 * The keys in the map represent the status codes returned by the EGK unlock operation.
 * The values in the map represent the corresponding response status.
 */
val unlockEgkStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x6983 to ResponseStatus.PUK_BLOCKED,
    0x63C0 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_00,
    0x63C1 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_01,
    0x63C2 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_02,
    0x63C3 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_03,
    0x63C4 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_04,
    0x63C5 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_05,
    0x63C6 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_06,
    0x63C7 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_07,
    0x63C8 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_08,
    0x63C9 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_09,
    0x6581 to ResponseStatus.MEMORY_FAILURE,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6985 to ResponseStatus.WRONG_PASSWORD_LENGTH,
    0x6A88 to ResponseStatus.PASSWORD_NOT_FOUND
)

/**
 * Variable storing the reference data status codes and their corresponding ResponseStatus.
 *
 * The changeReferenceDataStatus variable is a map that represents the reference data status codes and their corresponding
 * ResponseStatus values. Each entry in the map consists of a reference data status code (as the key) and the corresponding
 * ResponseStatus enum value (as the value).
 *
 * The reference data status codes are 16-bit hexadecimal values, and the ResponseStatus enum provides a set of predefined
 * response status codes.
 *
 * The changeReferenceDataStatus map is defined as follows:
 *
 * - 0x9000: ResponseStatus.SUCCESS
 * - 0x63C0: ResponseStatus.WRONG_SECRET_WARNING_COUNT_00
 * - 0x63C1: ResponseStatus.WRONG_SECRET_WARNING_COUNT_01
 * - 0x63C2: ResponseStatus.WRONG_SECRET_WARNING_COUNT_02
 * - 0x63C3: ResponseStatus.WRONG_SECRET_WARNING_COUNT_03 (oldSecret wrong)
 * - 0x6581: ResponseStatus.MEMORY_FAILURE
 * - 0x6982: ResponseStatus.SECURITY_STATUS_NOT_SATISFIED
 * - 0x6983: ResponseStatus.PASSWORD_BLOCKED
 * - 0x6985: ResponseStatus.WRONG_PASSWORD_LENGTH
 * - 0x6A88: ResponseStatus.PASSWORD_NOT_FOUND
 *
 * @see ResponseStatus    The enum class representing all response status codes
 */
val changeReferenceDataStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x63C0 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_00,
    0x63C1 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_01,
    0x63C2 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_02,
    0x63C3 to ResponseStatus.WRONG_SECRET_WARNING_COUNT_03, // oldSecret wrong
    0x6581 to ResponseStatus.MEMORY_FAILURE,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED,
    0x6983 to ResponseStatus.PASSWORD_BLOCKED,
    0x6985 to ResponseStatus.WRONG_PASSWORD_LENGTH,
    0x6A88 to ResponseStatus.PASSWORD_NOT_FOUND
)

/**
 * This variable represents the status codes returned by the `getRandomValues` operation.
 *
 * It is a map where the key is the status code value (in hexadecimal format) and the value is the corresponding response status.
 *
 * @see ResponseStatus
 * @see ResponseStatus.SUCCESS
 * @see ResponseStatus.SECURITY_STATUS_NOT_SATISFIED
 *
 */
val getRandomValuesStatus = mapOf(
    0x9000 to ResponseStatus.SUCCESS,
    0x6982 to ResponseStatus.SECURITY_STATUS_NOT_SATISFIED
)

/**
 * All response status codes
 * @see "gemSpec_COS_16.2"
 */
enum class ResponseStatus {
    // spec: gemSpec_COS_16.2
    SUCCESS,
    UNKNOWN_EXCEPTION,
    UNKNOWN_STATUS,
    DATA_TRUNCATED,
    CORRUPT_DATA_WARNING,
    END_OF_FILE_WARNING,
    END_OF_RECORD_WARNING,
    UNSUCCESSFUL_SEARCH,
    FILE_DEACTIVATED,
    FILE_TERMINATED,
    RECORD_DEACTIVATED,
    TRANSPORT_STATUS_TRANSPORT_PIN,
    TRANSPORT_STATUS_EMPTY_PIN,
    PASSWORD_DISABLED,
    AUTHENTICATION_FAILURE,
    NO_AUTHENTICATION,
    RETRY_COUNTER_COUNT_00,
    RETRY_COUNTER_COUNT_01,
    RETRY_COUNTER_COUNT_02,
    RETRY_COUNTER_COUNT_03,
    RETRY_COUNTER_COUNT_04,
    RETRY_COUNTER_COUNT_05,
    RETRY_COUNTER_COUNT_06,
    RETRY_COUNTER_COUNT_07,
    RETRY_COUNTER_COUNT_08,
    RETRY_COUNTER_COUNT_09,
    RETRY_COUNTER_COUNT_10,
    RETRY_COUNTER_COUNT_11,
    RETRY_COUNTER_COUNT_12,
    RETRY_COUNTER_COUNT_13,
    RETRY_COUNTER_COUNT_14,
    RETRY_COUNTER_COUNT_15,
    UPDATE_RETRY_WARNING_COUNT_00,
    UPDATE_RETRY_WARNING_COUNT_01,
    UPDATE_RETRY_WARNING_COUNT_02,
    UPDATE_RETRY_WARNING_COUNT_03,
    UPDATE_RETRY_WARNING_COUNT_04,
    UPDATE_RETRY_WARNING_COUNT_05,
    UPDATE_RETRY_WARNING_COUNT_06,
    UPDATE_RETRY_WARNING_COUNT_07,
    UPDATE_RETRY_WARNING_COUNT_08,
    UPDATE_RETRY_WARNING_COUNT_09,
    UPDATE_RETRY_WARNING_COUNT_10,
    UPDATE_RETRY_WARNING_COUNT_11,
    UPDATE_RETRY_WARNING_COUNT_12,
    UPDATE_RETRY_WARNING_COUNT_13,
    UPDATE_RETRY_WARNING_COUNT_14,
    UPDATE_RETRY_WARNING_COUNT_15,
    WRONG_SECRET_WARNING_COUNT_00,
    WRONG_SECRET_WARNING_COUNT_01,
    WRONG_SECRET_WARNING_COUNT_02,
    WRONG_SECRET_WARNING_COUNT_03,
    WRONG_SECRET_WARNING_COUNT_04,
    WRONG_SECRET_WARNING_COUNT_05,
    WRONG_SECRET_WARNING_COUNT_06,
    WRONG_SECRET_WARNING_COUNT_07,
    WRONG_SECRET_WARNING_COUNT_08,
    WRONG_SECRET_WARNING_COUNT_09,
    WRONG_SECRET_WARNING_COUNT_10,
    WRONG_SECRET_WARNING_COUNT_11,
    WRONG_SECRET_WARNING_COUNT_12,
    WRONG_SECRET_WARNING_COUNT_13,
    WRONG_SECRET_WARNING_COUNT_14,
    WRONG_SECRET_WARNING_COUNT_15,
    ENCIPHER_ERROR,
    KEY_INVALID,
    OBJECT_TERMINATED,
    PARAMETER_MISMATCH,
    MEMORY_FAILURE,
    WRONG_RECORD_LENGTH,
    CHANNEL_CLOSED,
    NO_MORE_CHANNELS_AVAILABLE,
    VOLATILE_KEY_WITHOUT_LCS,
    WRONG_FILE_TYPE,
    SECURITY_STATUS_NOT_SATISFIED,
    COMMAND_BLOCKED,
    KEY_EXPIRED,
    PASSWORD_BLOCKED,
    KEY_ALREADY_PRESENT,
    NO_KEY_REFERENCE,
    NO_PRK_REFERENCE,
    NO_PUK_REFERENCE,
    NO_RANDOM,
    NO_RECORD_LIFE_CYCLE_STATUS,
    PASSWORD_NOT_USABLE,
    WRONG_RANDOM_LENGTH,
    WRONG_RANDOM_OR_NO_KEY_REFERENCE,
    WRONG_PASSWORD_LENGTH,
    NO_CURRENT_EF,
    INCORRECT_SM_DO,
    NEW_FILE_SIZE_WRONG,
    NUMBER_PRECONDITION_WRONG,
    NUMBER_SCENARIO_WRONG,
    VERIFICATION_ERROR,
    WRONG_CIPHER_TEXT,
    WRONG_TOKEN,
    UNSUPPORTED_FUNCTION,
    FILE_NOT_FOUND,
    RECORD_NOT_FOUND,
    DATA_TOO_BIG,
    FULL_RECORD_LIST,
    MESSAGE_TOO_LONG,
    OUT_OF_MEMORY,
    INCONSISTENT_KEY_REFERENCE,
    WRONG_KEY_REFERENCE,
    KEY_NOT_FOUND,
    KEY_OR_PRK_NOT_FOUND,
    PASSWORD_NOT_FOUND,
    PRK_NOT_FOUND,
    PUK_NOT_FOUND,
    DUPLICATED_OBJECTS,
    DF_NAME_EXISTS,
    OFFSET_TOO_BIG,
    INSTRUCTION_NOT_SUPPORTED,
    PUK_BLOCKED
}
