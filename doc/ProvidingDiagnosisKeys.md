# Providing Diagnosis Keys

Each new downloaded Diagnosis Key file (please see [Downloading Diagnosis Keys](DownloadingDiagnosisKeys.md) section) is provided for exposure checking. The check is performed by [Exposure Notification API](https://www.google.com/covid19/exposurenotifications/) with exposure configuration options that tune the matching algorithm. In order to provide elastic architecture, the exposure configuration is obtained from ProteGo Save backend service.

Steps:
- Unanalyzed Diagnosis Key files are downloaded as described in [Downloading Diagnosis Keys](DownloadingDiagnosisKeys.md) section. Diagnosis Key files must be signed appropriately - the matching algorithm only runs on data that has been verified with the public key distributed by the device configuration mechanism
- Exposure configuration options are obtained from ProteGo Save backend service
- The new downloaded Diagnosis Key files and the exposure configuration options are provided for [Exposure Notification API](https://www.google.com/covid19/exposurenotifications/)
- When the data has been successfully provided for [Exposure Notification API](https://www.google.com/covid19/exposurenotifications/) then:
  - Information about the latest analyzed Diagnosis Key file timestamp is stored locally on a device. This information is used to select the future Diagnosis Key files that have not been analyzed yet – only files with higher Diagnosis Key timestamp are selected
  - All sent to analyze Diagnosis Key files are deleted from device internal storage
  - Diagnosis keys analyze will being performed in the near future by [Exposure Notification API](https://www.google.com/covid19/exposurenotifications/), after which application receive a broadcast with the exposure analyze result