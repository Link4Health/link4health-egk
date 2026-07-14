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
package de.link4health.egk.card

/**
 * Base exception for secure messaging protocol errors.
 */
open class SecureMessagingException(message: String) : IllegalArgumentException(message)

/**
 * Thrown when MAC verification fails during secure messaging decryption.
 */
class SecureMessagingMacException(message: String) : SecureMessagingException(message)

/**
 * Thrown when an APDU does not conform to the secure messaging protocol structure.
 */
class MalformedSecureMessagingApduException(message: String) : SecureMessagingException(message)
