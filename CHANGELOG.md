# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [4.0.0-beta-1] - 2026-07-28

The version jumps from 1.0.4 to 4.0.0 to align the eGK library with the CardLink SDK release line.
This is a pre-release; the final 4.0.0 will follow after beta feedback.

### Added

- Diagnostics API for structured, sanitized event reporting: `EgkDiagnostics` listener,
  `EgkDiagnosticEvent`, `EgkDiagnosticStatus`, `EgkDiagnosticOperation`, and
  `EgkFailureCategory` (`de.link4health.egk.diagnostics`). Pass a listener via
  `NfcHealthCard.connect(tag, diagnostics, timeoutMillis)` to observe NFC and PACE
  operations without exposing card data.
- Typed exception hierarchy replacing generic failures:
  - `PaceKeyExchangeException`, `PaceMacMismatchException` (MAC mismatch during PACE —
    almost always indicates a wrong CAN), `UnsupportedEgkCardException`
  - `SecureMessagingException`, `SecureChannelTerminatedException`
  - `NfcTransmitException`, `NfcChannelClosedException`
- `NfcHealthCard.connect` overload with configurable IsoDep timeout and diagnostics listener.
- PACE MAC verification step (`step4VerifyPcdAndPiccMac`) validating the card's MAC against
  the derived MAC before the trusted channel is used.
- Extensive unit test coverage for byte handling, response statuses, key derivation,
  identifiers, secure messaging, secure channel, and NFC diagnostics.

### Changed

- **Breaking:** `NfcHealthCard` constructor is now `internal`; obtain instances via
  `NfcHealthCard.connect(...)`.
- Hardened NFC layer: explicit connection-state checks, failure categorization
  (`EgkFailureCategory`), and transmit failure reporting in `NfcHealthCard`,
  `NfcCardChannel`, `NfcCardSecureChannel`, and `SecureMessaging`.
- `ICardChannel.establishTrustedChannel` now runs on `Dispatchers.IO` and fails with the
  typed PACE exceptions above.
- Publishing moved from the Link4Health Nexus repository to Google Artifact Registry.
- Toolchain upgrade: Kotlin 2.0.21 → 2.3.20, Java target 17 → 21, Android Gradle Plugin
  8.10.1 → 8.13.0, Gradle 8.10.2 → 8.13; Detekt, SonarQube, and OWASP Dependency-Check
  plugin updates.
- Documentation generation moved from `generateDoku.gradle.kts` into `buildSrc`
  (`DocumentationTasks.kt`).

### Removed

- `snakeyaml` dependency.
- Standalone `generateDoku.gradle.kts` build script.

### Fixed

- Gradle wrapper version mismatch.
- Build failure caused by the AGP version.
- Detekt plugin alias typo (`dedekt` → `detekt`).

## [1.0.4] and earlier

See the [GitHub releases](https://github.com/Link4Health/link4health-egk/releases) for
notes on previous versions.
