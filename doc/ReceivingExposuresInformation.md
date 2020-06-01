# Receiving Exposures Documentation

When Exposure Notification Framework finishes comparing diagnosis keys with keys stored on the device and it detects some exposures it informs about the end of analysis by broadcasting **ACTION_EXPOSURE_STATE_UPDATED** intent. In order to provide user a correct risk level that is shown in the PWA module application has to get detailed information about detected exposures and save them to the local, encrypted database.

Steps:

- Register a receiver to receive broadcasts of the ACTION_EXPOSURE_STATE_UPDATED intent - this action is called **only** when there is **at least one** exposure detected
  - Receiver: [ExposureNotificationBroadcastReceiver](../app/src/main/java/pl/gov/mc/protegosafe/receiver/ExposureNotificationBroadcastReceiver.kt)
- Get exposure information from Exposure Notification Framework in a one time Worker (BroadcastReceiver is short-time living so it's not a good idea to call long-running operations there) by passing a token from a received intent. Exposure Notification Framework displays a notification to the user each time this method is invoked (so user is informed that there were some exposures and application fetched details of them)
  - Worker: [ExposureStateUpdateWorker](../app/src/main/java/pl/gov/mc/protegosafe/worker/ExposureStateUpdateWorker.kt)
  - UseCase: [GetExposureInformationUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetAnalyzeResultUseCase.kt)
  - Repository function: [ExposureNotificationRepository.getExposureInformation(token: String)](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/ExposureNotificationRepository.kt)
  - Repository implementation: [ExposureNotificationRepositoryImpl.getExposureInformation(token: String)](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/ExposureNotificationRepositoryImpl.kt)
- The list of **ExposureInformation** objects is provided from Exposure Notification Framework
- The app saves every exposure information to **encrypted** database with **only** the following data:
  - Day level resolution that the exposure occurred
  - Length of exposure (in 5 minutes intervals, with a 30 minute maximum)
  - The total risk score calculated for the exposure
  - UseCase: [SaveMatchedTokenUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/SaveMatchedTokenUseCase.kt)
  - Repository function: [ExposureRepository.upsert(exposure: ExposureItem)](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/ExposureRepository.kt)
  - Repository implementation: [ExposureRepositoryImpl.upsert(exposure: ExposureItem)](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/ExposureRepositoryImpl.kt)

![Figure 1: Interaction of the mobile application with Exposure Notification Framework when receiving exposures information.](images/ReceivingExposuresInformationDiagram.png "Figure 1: Interaction of the mobile application with Exposure Notification Framework when receiving exposures information.") 
