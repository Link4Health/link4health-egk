# Introduction

Existing libraries for Android are unfortunately outdated. 
The commonly referenced repository is [E-Rezept-App-Android](https://github.com/gematik/E-Rezept-App-Android) by the E-Rezept App.


Currently, communication with the eGK via NFC relies partially on code from the E-Rezept App. 
To address this, sections of the E-Rezept App’s code have been extracted and repurposed into this Android library.
This is intended to enhance functionality and usability for CardLink applications.

The source code contained in this library aims to offer a robust solution by combining the traditional code base with relevant,
up-to-date counterparts to maintain compatibility and extend support. We trust this library will be of great assistance in your Android development projects involving CardLink and the eGK.

---------------------------------------------------------------------------------------------------------------------

> [!CAUTION]
> This piece of software is still under development. Until this note is gone - use with caution. :worried:


> [!CAUTION]
> 
> If you use Android Studio or InteliJ IDEA you must install the Checkstyle-Plugin this is used to keep the code style clean.


## Requirements Installation and Setup

To use this library, you must fulfill the following requirements:
```
compileSdk = "34"
minSDK = "30"
targetSdk = "34"
javaTarget = "17"
```

> [!CAUTION]
> 
> As of March 2023, security patches will only be provided for Android versions 11 and above. That is why we provide this SDK only for `minSDK = 30`!

### Adding Dependencies to Your Application

At the moment is not available over Maven Central, you can use the composite build feature from Gradle or build the library as aar
To provide the link4health-eGK-library to your mavenLocal repository, you must run these Gradle tasks:

Run this task first:

```./gradlew assembleRelease```

Run this task second:

```./gradlew publishLink4HealthEgkLibraryPublicationToMavenLocal```


### Proguard or R8 setup

The lirary provides a `consumer-rules.pro` file that contains the rules for this SDK. Your app does not need to set up any additional rules.

### Usage examples and API Documentation

coming soon

### Troubleshooting and FAQ

coming soon

### Versioning

This library adheres to Semantic Versioning (SemVer) standards. Version numbers are structured as MAJOR.MINOR.PATCH. Each element increases numerically based on the following principles:

- **MAJOR:** Introduces breaking changes, meaning that developers may need to alter their existing applications to ensure compatibility.

- **MINOR:** Adds new features but remains backward-compatible. New additions won’t disrupt existing functionality.

- **PATCH:** Implements backward-compatible bug fixes.

Every version release is documented thoroughly, with details regarding the features and fixes it includes.

When following SemVer principles, you should feel safe updating the SDK to any minor or patch version. This is because under SemVer, newer versions will not break your existing applications. All updates are carried out in a way that maintains the system’s predictability and adherence to Semantic Versioning standards.

For further information on Semantic Versioning, you can visit the [official website - SemVer](https://semver.org/).

### Licensing

The library is licensed under the European Union Public Licence (EUPL); every use of the library Sourcecode must be in compliance with the EUPL.

You will find more details about the EUPL here: [https://joinup.ec.europa.eu/collection/eupl](https://joinup.ec.europa.eu/collection/eupl)

Unless required by applicable law or agreed to in writing, software distributed under the EUPL is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the EUPL for the specific language governing permissions and limitations under the License.

### Information
This product uses the NVD API but is not endorsed or certified by the NVD.
