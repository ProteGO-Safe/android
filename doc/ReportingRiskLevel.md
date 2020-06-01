# Reporting Risk Level

Pass the highest risk level of user's exposure from last 14 days detected by Exposure Notification Framework to PWA.

Steps:

- PWA request reporting risk level
  - JS Bridge UseCase: [OnGetBridgeDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/OnGetBridgeDataUseCase.kt)
  - JS Bridge Data Type: [OutgoingBridgeDataType.ANALYZE_RESULT](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt)
- Get all data about exposures from local database
  - UseCase: [GetAnalyzeResultUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetAnalyzeResultUseCase.kt)
  - Repository function: [ExposureRepository.getAllResults()](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/ExposureRepository.kt)
  - Repository implementation: [ExposureRepositoryImpl.getAllResults()](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/ExposureRepositoryImpl.kt)
- Filter exposure with the highest risk score from exposures
  - UseCase: [GetAnalyzeResultUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetAnalyzeResultUseCase.kt)
- Calc risk level from risk score (max **riskScore** = 4096):
  - **riskScore** < 1500 -> **NO_RISK**
  - **riskScore** < 3000 -> **MIDDLE_RISK**
  - else -> **HIGH_RISK**
  - Enum function: [RiskLevelData.fromRiskScore(riskScore: Int)](../data/src/main/java/pl/gov/mc/protegosafe/data/model/RiskLevelData.kt)
- Pass calculated risk level to PWA by returning json with result model back through OnGetBridgeDataUseCase
  - JS Bridge UseCase: [OnGetBridgeDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/OnGetBridgeDataUseCase.kt)
  - JS Bridge Data Type: [OutgoingBridgeDataType.ANALYZE_RESULT](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt)
  - Json composer: [OutgoingBridgeDataResultComposer.composeAnalyzeResult(exposure: ExposureItem)](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataResultComposer.kt)
