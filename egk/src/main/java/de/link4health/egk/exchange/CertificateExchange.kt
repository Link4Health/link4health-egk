package de.link4health.egk.exchange

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

import de.link4health.egk.card.ICardChannel
import de.link4health.egk.cardobjects.Df
import de.link4health.egk.cardobjects.Mf
import de.link4health.egk.identifier.ApplicationIdentifier
import de.link4health.egk.identifier.FileIdentifier
import de.link4health.egk.command.*
import java.io.ByteArrayOutputStream

/**
 * Retrieves the certificate from the smart card.
 *
 * This method retrieves the certificate from the smart card by executing a series of commands on the provided card channel.
 * It first selects the application with the specified Application Identifier (AID) using the select method of HealthCardCommand.
 * Then, it selects the file with the specified File Identifier (FID) using the select method of HealthCardCommand.
 * It retrieves the certificate data by reading the data bytes from the smart card in multiple iterations until all data is read.
 * The retrieved data is stored in a buffer.
 * Finally, the data in the buffer is converted to a byte array and returned as the certificate.
 *
 * @return The certificate as a byte array.
 * @throws IllegalStateException if the certificate couldn't be read.
 */
fun ICardChannel.retrieveCertificate(): ByteArray {
    HealthCardCommand.select(ApplicationIdentifier(Df.Esign.AID)).executeSuccessfulOn(this)
    HealthCardCommand.select(
        FileIdentifier(Mf.Df.Esign.Ef.CchAutE256.FID),
        selectDfElseEf = false,
        requestFcp = true,
        fcpLength = EXPECTED_LENGTH_WILDCARD_EXTENDED
    ).executeSuccessfulOn(this)

    val buffer = ByteArrayOutputStream()
    var offset = 0
    while (true) {
        val response = HealthCardCommand.read(offset)
            .executeOn(this)

        val data = response.apdu.data

        if (data.isNotEmpty()) {
            buffer.write(data)
            offset += data.size
        }

        when (response.status) {
            ResponseStatus.SUCCESS -> {}
            ResponseStatus.END_OF_FILE_WARNING,
            ResponseStatus.OFFSET_TOO_BIG -> break

            else -> error("Couldn't read certificate: ${response.status}")
        }
    }

    return buffer.toByteArray()
}
