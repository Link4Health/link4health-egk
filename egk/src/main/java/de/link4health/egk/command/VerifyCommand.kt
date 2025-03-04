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

import de.link4health.egk.card.EncryptedPinFormat2
import de.link4health.egk.card.PasswordReference

private const val CLA = 0x00
private const val VERIFY_SECRET_INS = 0x20
private const val MODE_VERIFICATION_DATA = 0x00

/**
 * Command representing Verify Secret Command gemSpec_COS#14.6.6
 */
fun HealthCardCommand.Companion.verifyPin(
    passwordReference: PasswordReference,
    dfSpecific: Boolean,
    pin: EncryptedPinFormat2,
) =
    HealthCardCommand(
        expectedStatus = verifySecretStatus,
        cla = CLA,
        ins = VERIFY_SECRET_INS,
        p1 = MODE_VERIFICATION_DATA,
        p2 = passwordReference.calculateKeyReference(dfSpecific),
        data = pin.bytes,
    )
