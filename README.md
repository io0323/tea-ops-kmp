# TeaOps KMP Shared Module

This project contains the shared Kotlin Multiplatform (KMP) logic for the TeaOps system. 
It houses the core business logic, including:
- Temperature monitoring and trend detection (`TemperatureTrend`)
- Process definition management (`ProcessDefinitionIssue`)
- Digest and alert generation for operations (`MonitoringDigest`, `OperationAlertSummary`)

## Modules

- `shared/src/commonMain/`: The core shared application logic.
- `shared/src/commonTest/`: Unit tests and logic verification.

## Development

Currently, this repository is focused purely on exposing the `shared` KMP logic and models. 

### Testing

Tests can be executed across all platforms or targeted to specific platforms depending on the environment configuration.

```bash
# To run common tests (assuming Gradle or similar will be initialized in root)
./gradlew :shared:cleanTestDebugUnitTest :shared:testDebugUnitTest
```
