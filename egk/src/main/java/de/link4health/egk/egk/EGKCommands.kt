package de.link4health.egk.egk

import de.link4health.egk.card.CardKey
import de.link4health.egk.cardobjects.Ef
import de.link4health.egk.command.HealthCardCommand
import de.link4health.egk.command.read
import de.link4health.egk.command.select
import de.link4health.egk.command.selectInternal
import de.link4health.egk.identifier.ShortFileIdentifier

/**
 * This object provides utility methods for creating various HealthCardCommands for interacting with a health card.
 */
object EGKCommands {

    /** gemSpec_COS key reference for the symmetric card-to-card authentication key. */
    private const val INTERNAL_AUTHENTICATION_KEY_REFERENCE = 9

    /**
     * Selects the root of the object system, either by selecting the parent folder or the AID.
     * Can also read the File Control Parameter (FCP) if specified.
     *
     * @return a [HealthCardCommand] representing the select root command
     */
    fun selectRoot(): HealthCardCommand {
        return HealthCardCommand.select(selectParentElseRoot = false, readFirst = false)
    }

    /**
     * Selects the gemSpec_COS internal authentication key. Key reference 9 identifies
     * the symmetric card-to-card key; the zero OID selects the algorithm associated with it.
     *
     * @return A [HealthCardCommand] object representing the command to select the internal key
     */
    fun selectInternal(): HealthCardCommand {
        return HealthCardCommand.selectInternal(
            cardKey = CardKey(INTERNAL_AUTHENTICATION_KEY_REFERENCE),
            dfSpecific = false,
            oid = byteArrayOf(0),
        )
    }

    /**
     * Executes a Read ATR command on a health card.
     *
     * @param expectedLength the expected length of the ATR data
     * @return a [HealthCardCommand] object representing the read ATR command
     */
    fun commandReadATR(expectedLength: Int): HealthCardCommand {
        return HealthCardCommand.read(
            ShortFileIdentifier(Ef.Atr.SFID),
            ne = expectedLength,
            offset = 0,
        )
    }

    /**
     * Reads a binary file with the specified expected length.
     *
     * @param expectedLength the expected length of the binary file
     * @return a [HealthCardCommand] object representing the command to read the binary file
     */
    fun commandReadEFVersion2(expectedLength: Int): HealthCardCommand {
        return HealthCardCommand.read(
            ShortFileIdentifier(Ef.Version2.SFID),
            ne = expectedLength,
            offset = 0,
        )
    }

    /**
     * Executes the command to read the Global Data Object (GDO) from the health card.
     *
     * @param expectedLength the expected length of the response
     * @return a HealthCardCommand object representing the command to read the GDO
     */
    fun commandReadGDO(expectedLength: Int): HealthCardCommand {
        return HealthCardCommand.read(
            ShortFileIdentifier(Ef.Gdo.SFID),
            ne = expectedLength,
            offset = 0,
        )
    }

    /**
     * Reads a health card command with [ShortFileIdentifier] gemSpec_COS#14.3.2.2 and returns a [HealthCardCommand] object.
     *
     * @param expectedLength the expected length of the command
     * @return a [HealthCardCommand] object representing the read command
     */
    fun commandReadCvcCA(expectedLength: Int): HealthCardCommand {
        return HealthCardCommand.read(
            ShortFileIdentifier(Ef.CcaEgkCsE256.SFID),
            ne = expectedLength,
            offset = 0,
        )
    }

    /**
     * Reads the CEgkAutCVCE256 file from the health card.
     *
     * @param expectedLength the expected length of the response data
     * @return a [HealthCardCommand] object representing the command
     */
    fun commandReadCvcAuth(expectedLength: Int): HealthCardCommand {
        return HealthCardCommand.read(
            ShortFileIdentifier(Ef.CeEgkAutCVCE256.SFID),
            ne = expectedLength,
            offset = 0,
        )
    }

    /**
     * Reads the ESignCChAutR2048 file from the health card.
     * Needs select command before reading this.
     *
     * @param expectedLength the expected length of the response data
     * @return a [HealthCardCommand] object representing the command
     */
    fun commandReadX509AuthRSA(expectedLength: Int): HealthCardCommand {
        return HealthCardCommand.read(
            ShortFileIdentifier(Ef.ESignCChAutR2048.SFID),
            ne = expectedLength,
            offset = 0,
        )
    }

    /**
     * Reads the ESignCChAutE256 file from the health card.
     * Needs select command before reading this.
     *
     * @param expectedLength the expected length of the response data
     * @return a [HealthCardCommand] object representing the command
     */
    fun commandReadX509AuthECC(expectedLength: Int): HealthCardCommand {
        return HealthCardCommand.read(
            ShortFileIdentifier(Ef.ESignCChAutE256.SFID),
            ne = expectedLength,
            offset = 0,
        )
    }
}
