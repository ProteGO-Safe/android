# Removing historical data

User can clear historical data collected by Exposure Notification Framework and the app.

Steps:

- PWA requests deleting all data
  - JS Bridge UseCase: [OnSetBridgeDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/OnSetBridgeDataUseCase.kt)
  - JS Bridge Data Type:  [OutgoingBridgeDataType.REQUEST_CLEAR_EXPOSURE_NOTIFICATIONS_DATA](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/IncomingBridgeDataType.kt)
- Clear local database
  - UseCase: [ClearExposureNotificationDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/ClearExposureNotificationDataUseCase.kt)
  - Repository function: [ExposureRepository.nukeDb()](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/ExposureRepository.kt)
  - Repository implementation: [ExposureRepositoryImpl.nukeDb()](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/ExposureRepositoryImpl.kt)
- Redirect user to Exposure Notification Framework settings page, where they can clear data collected by Exposure Notification Framework
  - View function: [HomeFragment.requestClearData()](../app/src/main/java/pl/gov/mc/protegosafe/ui/home/HomeFragment.kt)