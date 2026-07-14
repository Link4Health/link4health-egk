# 4.0.0-beta-1

The version jumps from 1.0.4 to 4.0.0 to align the eGK library with the CardLink SDK release line.
This is a pre-release; the final 4.0.0 will follow after beta feedback.

## Added

- Diagnostics API for structured, sanitized event reporting: `EgkDiagnostics` listener, `EgkDiagnosticEvent`, `EgkDiagnosticStatus`, `EgkDiagnosticOperation`, and `EgkFailureCategory`. Pass a listener via `NfcHealthCard.connect(tag, diagnostics, timeoutMillis)` to observe NFC and PACE operations without exposing card data.
- Typed exception hierarchy: `PaceKeyExchangeException`, `PaceMacMismatchException` (almost always indicates a wrong CAN), `UnsupportedEgkCardException`, `SecureMessagingException`, `SecureChannelTerminatedException`, `NfcTransmitException`, `NfcChannelClosedException`.
- `NfcHealthCard.connect` overload with configurable IsoDep timeout and diagnostics listener.
- PACE MAC verification step validating the card's MAC against the derived MAC before the trusted channel is used.
- Extensive unit test coverage for byte handling, response statuses, key derivation, identifiers, secure messaging, secure channel, and NFC diagnostics.

## Changed

- **Breaking:** `NfcHealthCard` constructor is now `internal`; obtain instances via `NfcHealthCard.connect(...)`.
- Hardened NFC layer: explicit connection-state checks, failure categorization, and transmit failure reporting.
- `ICardChannel.establishTrustedChannel` now runs on `Dispatchers.IO` and fails with the typed PACE exceptions above.
- Publishing moved from the Link4Health Nexus repository to Google Artifact Registry.
- Toolchain upgrade: Kotlin 2.3.20, Java target 21, AGP 8.13.0, Gradle 8.13.

## Removed

- `snakeyaml` dependency and the standalone `generateDoku.gradle.kts` build script.

## Fixed

- Gradle wrapper version mismatch and a build failure caused by the AGP version.
