# JavaScript Bridge Documentation

When application is running, it uses [NativeBridgeInterface](../app/src/main/java/pl/gov/mc/protegosafe/ui/home/NativeBridgeInterface.kt) to interact with web application(PWA).

- JavaScript → Native:
  - Set data: [NativeBridgeInterface.setBridgeData(dataType: Int, data: String)](../app/src/main/java/pl/gov/mc/protegosafe/ui/home/NativeBridgeInterface.kt)
  - Get data: [NativeBridgeInterface.getBridgeData(dataType: Int, data: String, requestId: String): String](../app/src/main/java/pl/gov/mc/protegosafe/ui/home/NativeBridgeInterface.kt)
- Native -> JavaScript:
  - Set data: [HomeViewModel.onBridgeData(dataType: Int, dataJson: String)](../app/src/main/java/pl/gov/mc/protegosafe/ui/home/HomeViewModel.kt)
  
Contract with PWA:

- function getBridgeData is handled by [OnGetBridgeDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/OnGetBridgeDataUseCase.kt)

|Data type|Description|Response JSON format|
| ------ | --- | --- |
| [OutgoingBridgeDataType.NOTIFICATION_DATA](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt) | Request content of current notification <br>UseCase [GetNotificationDataAndClearUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetNotificationDataAndClearUseCase.kt) | `{`<br>`"data": "JSON with notification data"`<br>`}` |
| [OutgoingBridgeDataType.ANALYZE_RESULT](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt) | Request the highest risk level of exposure <br>UseCase[GetAnalyzeResultUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetAnalyzeResultUseCase.kt) | `{`<br>`"riskLevel" : 1 //1- no risk, 2-middle risk, 3-high risk`<br>`}` |
| [OutgoingBridgeDataType.SERVICES_STATUS](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt) | Request statuses of services <br>UseCase[GetServicesStatusUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetServicesStatusUseCase.kt) |`"servicesStatus": {`<br>`"exposureNotificationStatus": 1 - ON, 2 - OFF, 3 - NOT SUPPORTED,`<br>`"isLocationOn": true/false,`<br>`"isBtOn": true/false,`<br>`"isNotificationEnabled": true/false`<br>`}`|

  Parameters in service status response:

  |Json parameter|Description|
  | ------ | --- |
  | exposureNotificationStatus | Status of Exposure Notification Framework |
  | isLocationOn | Is Location service enabled |
  | isBtOn | Is Bluetooth service enabled |
  | isNotificationEnabled | Can notification be presented |

- function setBridgeData is handled by [OnSetBridgeDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/OnSetBridgeDataUseCase.kt)

|Data type|Description|Request JSON format|
| ------ | --- | --- |
| [IncomingBridgeDataType.TRIAGE](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/IncomingBridgeDataType.kt) | Save triage finished timestamp <br>UseCase[SaveTriageCompletedUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/SaveTriageCompletedUseCase.kt) | `{`<br>`“timestamp” = 1589669050`<br>`}` |
| [IncomingBridgeDataType.REQUEST_CLEAR_EXPOSURE_NOTIFICATIONS_DATA](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/IncomingBridgeDataType.kt) | Clear application data <br>UseCase[ClearExposureNotificationDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/ClearExposureNotificationDataUseCase.kt) | `{`<br>`“clearBtData” = true/false`<br>`}` |
| [IncomingBridgeDataType.REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/IncomingBridgeDataType.kt) | Upload Temporary Exposure Keys <br>UseCase[UploadTemporaryExposureKeysUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/UploadTemporaryExposureKeysUseCase.kt) | `{`<br>`“pin” : “123FAA”`<br>`}` |
| [IncomingBridgeDataType.REQUEST_SERVICE_STATUS_CHANGE](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/IncomingBridgeDataType.kt) | Enable mentioned service <br>UseCase[ChangeServiceStatusUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/ChangeServiceStatusUseCase.kt) | `{`<br>`{“enableExposureNotificationService” = true/false, //optional`<br>`"enableBt" = true, //optional`<br>`"enableLocation" = true, //optional`<br>`"enableNotification" = true //optional`<br>`}`<br>`}` |

  Parameters in service status change request:

  |Json parameter|Description|
  | ------ | --- |
  | enableExposureNotificationService | Enable/Disable Exposure Notification Framework |
  | enableBt | Enable Bluetooth service |
  | enableLocation | Enable Location service |
  | enableNotification | Enable notifications |
  
- function onBridgeData

|Data type|Description|JSON format|
| ------ | --- | --- |
| [OutgoingBridgeDataType.APP_LIFECYCLE_STATE](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt) | Send lifecycle state <br>UseCase [ComposeAppLifecycleStateBrideDataUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/ComposeAppLifecycleStateBrideDataUseCase.kt) | `{`<br>`“appState” : 1(Active)/2(Inactive)`<br>`}` |
| [OutgoingBridgeDataType.TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt) | Send state of uploading process <br>UseCase [UploadTemporaryExposureKeysWithCachedPayloadUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/UploadTemporaryExposureKeysWithCachedPayloadUseCase.kt) | `{`<br>`“result“ : 1(SUCCESS)/2(FAILED),3(PROBLEM)`<br>`}` |
| [OutgoingBridgeDataType.SERVICES_STATUS](../domain/src/main/java/pl/gov/mc/protegosafe/domain/model/OutgoingBridgeDataType.kt) | Send statuses of services <br>UseCase [GetServicesStatusUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetServicesStatusUseCase.kt) |`"servicesStatus": {`<br>`"exposureNotificationStatus": 1 - ON, 2 - OFF, 3 - NOT SUPPORTED,`<br>`"isLocationOn": true/false,`<br>`"isBtOn": true/false,`<br>`"isNotificationEnabled": true/false`<br>`}`|
