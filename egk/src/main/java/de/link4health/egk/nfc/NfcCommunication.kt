package de.link4health.egk.nfc

/**
 * Describes the major steps of an NFC card communication flow.
 */
enum class NfcCommunication {
    NOTHING,
    READ_ATR,
    READ_VERSION2,
    READ_GDO,
    READ_CVC_CA,
    READ_CVC_AUTH,
    READ_X_509_AUTH,
    SELECT_ROOT,
    SELECT_INTERNAL,
    INTERNAL_AUTHENTICATION,
    FINISH,
}
