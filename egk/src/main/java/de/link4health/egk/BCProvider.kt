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

package de.link4health.egk

import org.bouncycastle.jce.provider.BouncyCastleProvider

/**
 * The BCProvider variable represents an instance of the BouncyCastleProvider class from the Bouncy Castle cryptographic library.
 * This provider is used for cryptographic operations in the application.
 *
 * Usage Example:
 *
 * val provider = BCProvider
 *
 * Note: The BCProvider variable is not intended to be modified or reassigned.
 */
val BCProvider = BouncyCastleProvider()
