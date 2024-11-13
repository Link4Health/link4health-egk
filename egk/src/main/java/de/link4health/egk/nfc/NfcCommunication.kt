/*
 *  (C)opyright 2024 eHealth experts GmbH
 *
 *  This code was created by Normen Düring, owned by eHealth experts GmbH.
 *  Any unauthorized use, distribution, reproduction, or disclosure
 *  of this code, or any parts of it, in any form is prohibited.
 *
 *  For any questions, comments or suggestions, please contact normen.duering@ehealthexperts.de
 */
package de.link4health.egk.nfc

/**
 * Represents the loading status for the SDK.
 */
enum class NfcCommunication {
    NOTHING,
    READ_ATR,
    READ_VERSION2,
    READ_GTO,
    READ_CVC_CA,
    READ_CVC_AUTH,
    READ_X_509_AUTH,
    SELECT_ROOT,
    SELECT_INTERNAL,
    INTERNAL_AUTHENTICATION,
    FINISH,
}
