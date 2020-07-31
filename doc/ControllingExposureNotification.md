# Controlling Exposure Notification

Enables users to start and stop broadcasting and scanning, indicate if [Exposure notification Framework](https://www.google.com/covid19/exposurenotifications/) are running.

- Start the Exposure Notification Framework broadcasting and scanning process.
  - PWA requests start of Exposure Notification Framework
  - UseCase [StartExposureNotificationUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/StartExposureNotificationUseCase.kt).
  - Repository function: [ExposureNotificationRepository.start()](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/ExposureNotificationRepository.kt)
  - Repository implementation: [ExposureNotificationRepositoryImpl.start()](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/ExposureNotificationRepositoryImpl.kt)
    - If not previously started, Exposure Notification Framework shows a user dialog for consent to start exposure detection and get permission.
    - If user grants the required permission, Exposure Notification Framework shows a user dialog to enable Location nad Bluetooth services. This services are required to detect nearby Bluetooth devices, however Exposure Notification Framework and the app don't use device location.
  - If permission is granted and required services are enabled, Exposure Notification Framework will start broadcasting and scanning.
    - All of Exposure Notification Framework methods are now available.
    - Pass Exposure Notification Framework status - [ExposureNotificationStatusItem.ON](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/ExposureNotificationStatusItem.kt) - to PWA.
  - If user deny on any of Exposure Notification Framework requests, onboarding is skipped and Exposure Notification Framework disabled.
    - Pass Exposure Notification Framework status - [ExposureNotificationStatusItem.OFF](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/ExposureNotificationStatusItem.kt) - to PWA.
  - If Exposure Notification Framework is not available on user's device
    - Pass Exposure Notification Framework status - [ExposureNotificationStatusItem.NOT_SUPPORTED](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/ExposureNotificationStatusItem.kt) - to PWA.
  - If any of the required services is disabled during Exposure Notification Framework work
    - Broadcasting and scanning process are stopped.
    - Pass services status - UseCase [GetServicesStatusUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetServicesStatusUseCase.kt) - to PWA to inform user that something is missing.
    - Exposure Notification Framework shows a user dialog to enable required service.
- Indicate if Exposure Notification Framework is running.
  - Repository function: [ExposureNotificationRepository.isEnabled()](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/ExposureNotificationRepository.kt)
  - Repository implementation: [ExposureNotificationRepositoryImpl.isEnabled()](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/ExposureNotificationRepositoryImpl.kt)
- Disable broadcasting and scanning.
  - UseCase [StopExposureNotificationUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/StopExposureNotificationUseCase.kt)
  - Repository function: [ExposureNotificationRepository.stop()](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/ExposureNotificationRepository.kt)
  - Repository implementation: [ExposureNotificationRepositoryImpl.stop()](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/ExposureNotificationRepositoryImpl.kt)
    - Contents of the Exposure Notification Framework database and keys will remain.
    - If the app has been uninstalled by the user, this will be automatically invoked and the database and keys will be wiped from the device.