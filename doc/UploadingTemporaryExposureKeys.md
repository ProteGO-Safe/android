# Temporary Exposure Keys Upload

Once the user is diagnosed with Covid-19 they can share their Temporary Exposure Keys(**TEK**) to inform others, with whom they had contact with, about potential exposition to virus.

Steps:

- User diagnosed with Covid-19 is given a unique 6-character code (**PIN**) from the authority employee
- User enters the code and agrees for TEK upload (**PWA** module)
- PWA module notifies native code through JS bridge that provided **PIN** should be used for **TEK**s upload
- Exposure Notification Framework asks the user for a permission to share TEK with STOP COVID - ProteGO Safe App
- The app checks if 6-character code is valid by requesting STOP COVID - ProteGO Safe server for a **Token** (proper Firebase Cloud Function is called with **PIN** as a payload):
  - UseCase: [UploadTemporaryExposureKeysUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/UploadTemporaryExposureKeysUseCase.kt)
  - Repository function: [TemporaryExposureKeysUploadRepository.getAccessToken(pinItem: PinItem)](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/TemporaryExposureKeysUploadRepository.kt)
  - Repository implementation: [TemporaryExposureKeysUploadRepositoryImpl.getAccessToken(pinItem: PinItem)](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/TemporaryExposureKeysUploadRepositoryImpl.kt)
- The app generates Verification Payload(**VP**) using SafetyNet Attestation API. It helps to determine whether the server is interacting with a genuine app.
  - UseCase: [GetSafetyNetAttestationTokenUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/GetSafetyNetAttestationTokenUseCase.kt)
  - Manager function: [SafetyNetAttestationWrapper.attestFor(keys: List<DiagnosisKey>, regions: List<String>)](../domain/src/main/java/pl/gov/mc/protegosafe/domain/manager/SafetyNetAttestationWrapper.kt)
  - Manager implementation: [SafetyNetAttestationWrapperImpl.attestFor(keys: List<DiagnosisKey>, regions: List<String>)](../device/src/main/java/pl/gov/mc/protegosafe/manager/SafetyNetAttestationWrapperImpl.kt)
- The app generates Upload Request Payload(**URP**) with the following data:
  - array of Temporary Exposure Keys
  - **Token**
  - Verification Payload
  - Platform name("Android")
  - Region("PL")
  - App package name("pl.gov.mc.protegosafe")
- The app uploads URP to STOP COVID - ProteGO Safe server with a proper Firebase Cloud Function
  - UseCase: [UploadTemporaryExposureKeysUseCase](../domain/src/main/java/pl/gov/mc/protegosafe/domain/usecase/UploadTemporaryExposureKeysUseCase.kt)
  - Repository function: [TemporaryExposureKeysUploadRepository.uploadTemporaryExposureKeys(requestData: TemporaryExposureKeysUploadRequestData)](../domain/src/main/java/pl/gov/mc/protegosafe/domain/repository/TemporaryExposureKeysUploadRepository.kt)
  - Repository implementation: [TemporaryExposureKeysUploadRepositoryImpl.uploadTemporaryExposureKeys(requestData: TemporaryExposureKeysUploadRequestData)](../data/src/main/java/pl/gov/mc/protegosafe/data/repository/TemporaryExposureKeysUploadRepositoryImpl.kt)
  
  


![Figure 1: Interaction of the mobile application with the backend server and Exposure Notification Framework during upload.](images/TemporaryExposureKeysUploadDiagram.png "Figure 1: Interaction of the mobile application with the backend server and Exposure Notification Framework during upload.")
