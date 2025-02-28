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

package de.link4health.egk.cardobjects

/**
 * eGK 2.1 file system objects
 * @see gemSpec_eGK_ObjSys_G2_1_V4_0_0 'Spezifikation der eGK Objektsystem G2.1'
 */

/**
 * This class defines constants for the file identifiers (FID) and short file identifiers (SFID) for various files on a health card.
 */
object Ef {
    /**
     * Represents the Card Access object.
     */
    object CardAccess {
        const val FID = 0x011C
        const val SFID = 0x1C
    }

    /**
     * The Version class represents the version information of the software.
     *
     * This class contains constant values for the file ID (FID) and sub-file ID (SFID).
     * The FID is represented as 0x2F10, and the SFID is represented as 0x10.
     */
    object Version {
        const val FID = 0x2F10
        const val SFID = 0x10
    }

    /**
     * Singleton object representing the Version2 class.
     */
    object Version2 {
        const val FID = 0x2F11
        const val SFID = 0x11
    }

    /**
     * The Atr object represents the ATR (Answer to Reset) protocol data for a health card.
     *
     * @property FID The file identifier (FID) for ATR. The value is 0x2F01.
     * @property SFID The short file identifier (SFID) for ATR. The value is 0x1D.
     */
    object Atr {
        const val FID = 0x2F01
        const val SFID = 0x1D
    }

    /**
     * Represents a class CcaEgkCsE256.
     * The class contains constants for FID and SFID values.
     */
    object CcaEgkCsE256 {
        const val FID = 0x2F07
        const val SFID = 0x07
    }

    /**
     * CeEgkAutCVCE256 is an object that represents the CEgkAutCVCE256 file in a health card.
     */
    object CeEgkAutCVCE256 {
        const val FID = 0x2F06
        const val SFID = 0x06
    }

    /**
     * The `Dir` class represents a directory.
     *
     * It provides constants for the directory identifier (FID) and subfile identifier (SFID).
     * The directory identifier indicates the identification of the directory,
     * while the subfile identifier indicates the identification of a specific subfile within the directory.
     *
     * @property FID The directory identifier.
     * @property SFID The subfile identifier.
     */
    object Dir {
        const val FID = 0x2F00
        const val SFID = 0x1E
    }

    /**
     * Represents the GDO (Global Data Object) class.
     * This object contains constants related to the GDO.
     */
    object Gdo {
        const val FID = 0x2F02
        const val SFID = 0x02
    }

    /**
     * This class represents the NFD (Normalized File Descriptor) object.
     * It provides constants for the FID (File Identifier) and SFID (Special File Identifier).
     */
    object Nfd {
        const val FID = 0xD010
        const val SFID = 0x10
    }

    /**
     * The HCaeinwilligung class represents the consent for handling personal data.
     *
     * It provides constants for the Feature Identifier (FID) and Sub-Feature
     * Identifier (SFID) related to consent.
     */
    object HCaeinwilligung {
        const val FID = 0xD005
        const val SFID = 0x05
    }

    /**
     * Represents the HcaGVD class.
     */
    object HcaGVD {
        const val FID = 0xD003
        const val SFID = 0x03
    }

    /**
     * The `HcaLogging` class provides constants related to logging in the HCA system.
     */
    object HcaLogging {
        const val FID = 0xD006
        const val SFID = 0x06
    }

    /**
     * This class represents the HcaPD protocol data.
     * It provides constants related to FID and SFID.
     */
    object HcaPD {
        const val FID = 0xD001
        const val SFID = 0x01
    }

    /**
     * Represents a class for managing HCA Pruefungsnachweis.
     */
    object HcaPruefungsnachweis {
        const val FID = 0xD01C
        const val SFID = 0x1C
    }

    /**
     * The HcaStandalone class represents a standalone instance of the HCA (Hydroxycitric Acid) system.
     *
     * The HCAStandalone object provides constants related to the standalone instance such as FID and SFID.
     */
    object HcaStandalone {
        const val FID = 0xDA0A
        const val SFID = 0x0A
    }

    /**
     * HcaStatusVD is a singleton object that represents the status of Hca (Host Controller Adapter) Virtual Disk.
     * It provides constants related to the Hca status.
     */
    object HcaStatusVD {
        const val FID = 0xD00C
        const val SFID = 0x0C
    }

    /**
     * Singleton class representing the HcaTTN protocol.
     */
    object HcaTTN {
        const val FID = 0xD00F
        const val SFID = 0x0F
    }

    /**
     * HcaVD is a singleton object that provides constants related to HCA VD.
     */
    object HcaVD {
        const val FID = 0xD002
        const val SFID = 0x02
    }

    /**
     * This object represents the HCA Verweis class.
     *
     * The HCA Verweis class provides constants related to the verweis field code.
     */
    object HcaVerweis {
        const val FID = 0xD009
        const val SFID = 0x09
    }

    /**
     * Represents a class for ESignCChAutR2048. It contains two constant properties.
     *
     * @property FID The file identifier of ESignCChAutR2048.
     * @property SFID The short file identifier of ESignCChAutR2048.
     */
    object ESignCChAutR2048 {
        const val FID = 0xC500
        const val SFID = 0x01
    }

    /**
     * This object represents the ESignCChAutE256 class.
     */
    object ESignCChAutE256 {
        const val FID = 0xC504
        const val SFID = 0x04
    }

    /**
     * This class represents the ESignCChAutnR2048 module.
     *
     * It provides the following constants:
     * - `FID`: Represents the File ID of the module, which is 0xC509.
     * - `SFID`: Represents the Sub-File ID of the module, which is 0x09.
     */
    object ESignCChAutnR2048 {
        const val FID = 0xC509
        const val SFID = 0x09
    }

    /**
     * ESignCChEncR2048 class represents the encryption module for E-Signature with RSA 2048-bit encryption.
     *
     * This class provides constants and functions related to the encryption process.
     *
     * @property FID The File ID associated with the encryption module. It is represented as a hex value (0xC200).
     * @property SFID The Sub-File ID associated with the encryption module. It is represented as a hex value (0x02).
     */
    object ESignCChEncR2048 {
        const val FID = 0xC200
        const val SFID = 0x02
    }

    /**
     * The `ESignCChEncvR2048` class represents an object that provides constant values related to E-signature encryption.
     *
     * The class provides the following constants:
     * - `FID`: The identifier of the E-signature encryption format.
     * - `SFID`: The sub-identifier of the E-signature encryption format.
     */
    object ESignCChEncvR2048 {
        const val FID = 0xC50A
        const val SFID = 0x0A
    }
}

/**
 * Object that contains constants related to the Df class.
 */
object Df {
    object Esign {
        const val AID = "A000000167455349474E"
    }

    object HCA {
        const val AID = "D27600000102"

        object Ef {
            object PD {
                const val FID = 0xD001
                const val SFID = 0x01
            }

            object VD {
                const val FID = 0xD002
                const val SFID = 0x02
            }
        }
    }
}

/**
 * The Mf class represents the Master File (MF) in a smart card.
 * It contains nested objects that represent different parts of the MF hierarchy.
 *
 * The hierarchy structure is as follows:
 * - Mf.MrPinHome
 * - Mf.Df
 *   - Mf.Df.Esign
 *     - Mf.Df.Esign.Ef
 *       - Mf.Df.Esign.Ef.CchAutE256
 *     - Mf.Df.Esign.PrK
 *       - Mf.Df.Esign.PrK.ChAutE256
 */
object Mf {
    /**
     * The MrPinHome class represents the home of Mr. Pin.
     */
    object MrPinHome {
        const val PWID = 0x02
    }

    /**
     * The `Df` class represents a collection of related objects and constants related to the `Df` functionality.
     */
    object Df {
        /**
         * Utility class for accessing electronic signatures (e-signs).
         *
         * This class provides constants for the file and key identifiers related to e-signs.
         *
         * @see Esign.Ef.CchAutE256
         * @see Esign.PrK.ChAutE256
         */
        object Esign {
            /**
             * The Ef class contains constants related to EF (Elementary File) files.
             */
            object Ef {
                /**
                 * Represents a class that contains constants related to the CchAutE256 file in a smart card.
                 *
                 * The CchAutE256 object provides the following constants:
                 * - FID: The file identifier (0xC504) of the CchAutE256 file.
                 * - SFID: The short file identifier (0x04) of the CchAutE256 file.
                 */
                object CchAutE256 {
                    const val FID = 0xC504
                    const val SFID = 0x04
                }
            }

            /**
             * PrK is a utility class that provides access to various constants and values.
             *
             * This class contains an inner class ChAutE256, which defines constants related to the ChAutE256 algorithm.
             *
             * The ChAutE256 algorithm is used for encryption and decryption operations, and the inner class provides a constant value KID which represents the algorithm identifier
             * .
             *
             * @see PrK.ChAutE256
             */
            object PrK {
                /**
                 * The ChAutE256 class represents a cryptographic algorithm for securely exchanging data.
                 *
                 * This class provides a constant value for the Key Identifier (KID).
                 */
                object ChAutE256 {
                    const val KID = 0x04
                }
            }
        }
    }
}
