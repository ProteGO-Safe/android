# STOP COVID - ProteGO Safe Android app

![Logo](./image.png "ProtegoSafe")

## Project overview

This is an Android application for [STOP COVID - ProteGO Safe project](https://github.com/ProteGO-Safe/specs) and it implements two main features:
* User daily triage - //TODO description to be provided or linked to main documentation//
* Contact tracing - module that is fully based on [Exposure Notification API](https://www.google.com/covid19/exposurenotifications/) provided by Google and Apple and it's goal is to inform people of potential exposure to COVID-19

Application is structured based on Clean Architecture pattern and Presentation (UI) layer is almost fully realized with a single Fragment (HomeFragment) with a WebView control that loads a website application called 'PWA'. PWA is responsible for GUI, user interaction and 'User daily triage' feature. A website app interacts with a native code through the JavaScript bridge and is described widely [here](doc/JavaScriptBridge.md). Native application business logic is implemented in a HomeViewModel and it decides what kind of UseCase should be executed based on application state. UseCases are defined in a Domain layer and they call methods from proper repository. Repositories implementations are either in a Data module (responsible for everything that is data related - storing, fetching or manipulation) or Device module (device specific APIs not directly related to data).

App implements contact tracing module that is based on [ExposureNotification API](https://www.google.com/covid19/exposurenotifications/) (EN) and we can extract couple of features related to this:
* [Controlling EN](doc/ControllingExposureNotification.md): start, stop, check if device supports it, check what is its state
* [Uploading Temporary Exposure Keys](doc/UploadingTemporaryExposureKeys.md) (TEKs) of positively diagnosed user verified by the application: authorize user for TEKs upload, get TEKs from EN, add proper verification data (using SafetyNet API), upload data to the [Cloud Server](https://github.com/ProteGO-Safe/backend).
* [Downloading](doc/DownloadingDiagnosisKeys.md) periodically files with batch of TEKs of positively diagnosed users (that recently uploaded their TEKs): execute periodic task responsible for downloading recently created .zip files (it fetches list of available files from CDN, selects only not yet analyzed files and downloads only these ones)
* [Providing files](doc/ProvidingDiagnosisKeys.md) to EN API for detecting exposures: get proper configuration for risk calculation (Exposure Configuration), fire EN API with list of downloaded files and configuration, delete analyzed files
* [Receiving information](doc/ReceivingExposuresInformation.md) about detected exposures: register broadcast receiver about exposures, get information about exposures, store part of information (day of exposure, risk score and duration that is in 5 minutes intervals but max 30 minutes)
* [Reporting risk level](doc/ReportingRiskLevel.md) to the PWA: extract risk scores of saved exposures and calculate risk level, pass risk level to PWA
* [Removing historical data](doc/RemovingHistoricalData.md): remove information about exposures older than 14 days


## Project structure

This is multi-module Android Studio Project. It can be compiled with gradle commands, or from Android Studio user interface.

## Project modules

- app -  all the classes related to the Android UI such as view models, adapters, views, dependency injection
- domain - in this module we place all objects that will interact with other layers
- data - contains everything related to data persistence and manipulation
- device - has everything related to Android that’s not data persistence and UI

## Build Variants

Application has 3 flavours matching environments: Prod, Stage, and Dev.

Flavours have different:

- Firebase configurations
- URL for PWA

There are two build types: release, and debug.

## Setup of the app
To get started on the app, setup and configure the following:
1. ./gradle.properties

2. Firebase - google-services.json


### Config parameters in gradle.properties

| Name                               | Description                                                  |
| ---------------------------------- | ------------------------------------------------------------ |
| STAGING_GET_ACCESS_TOKEN_ENDPOINT  | Endpoint for HTTPS call to validate PIN and get proper access token for TEKs upload (development type of application)                              |
| STAGING_UPLOAD_BUCKET_ENDPOINT     | Endpoint for HTTPS call to upload TEKs (development type of application)                                 |
| STAGING_STORAGE_BUCKET_ENDPOINT    | Endpoint for CDN with Diagnosis Keys (development type of application)                                   |
| STAGING_SAFETYNET_API_KEY          | SafetyNet device attestation API Key (development type of application)                                 |
| PRODUCTION_GET_ACCESS_TOKEN_ENDPOINT| Endpoint for HTTPS call to validate PIN and get proper access token for TEKs upload (production type of application)                              |
| PRODUCTION_UPLOAD_BUCKET_ENDPOINT  | Endpoint for HTTPS call to upload TEKs (production type of application)                         |
| PRODUCTION_STORAGE_BUCKET_ENDPOINT | Endpoint for CDN with Diagnosis Keys (production type of application)                                       |
| PRODUCTION_SAFETYNET_API_KEY       | SafetyNet device attestation API Key (production type of application)                                    |
| SHARED_PREFERENCES_FILE_NAME       | File name for Shared Preferences storage                     |

### Firebase and google-services.json
Setup Firebase for the different environment.
Download the google-services.json for each of environment and put into proper directory:

Prod: ./app/src/prod/google-services.json

Stage: ./app/src/stage/google-services.json

Dev: ./app/src/dev/google-services.json

---

## ChangeLog

**4.13.0**
- Added handling deep links
- Added handling redirect to sms application
- Updated UI

**4.12.0**

- Disabled SafetyNet token check for lab test 
- Updated UI

**4.11.0**

- Updated UI

**4.10.1**

- Fix missing districts

**4.10.0**

- Added new file storage method
- Split current JSON data to multiple smaller data files to prevent over downloading unwanted data
- Enhanced view of the app home screen, which now includes more detailed statistics on vaccination and infections
- New screen with detailed statistics and graphs on vaccination (number of people vaccinated, doses, adverse reactions) and infections (number of people infected, recovered, deaths, causes of death and tests)
- Added information on vaccination and registration rules with redirection to registration, vaccination request and helpline
- Fixed bug that occurred on Xiaomi phones with dark mode enabled
- Bump EN version to 1.8.3

**4.9.1**

- Added information on vaccination statistics in Poland
- Updated UI


**4.9.0**

- Added information on COVID-19 statistics in Poland
- Added information about the checks carried out by the application on meetings with people who reported to have got sick with COVID-19
- Added history of application activity (information about performed checks, indicated threats, system notifications)
- Added possibility of redirecting to a specific application screen by system notification
- Added possibility to evaluate applications in the Google Play / Apple App Store
- Added translations of the application into Russian, German and Turkish.
- Updated UI


**4.8.0**

- Possibility to share Temporary Exposure Keys with other countries
- Updated UI


**4.7.1**

- Fixed back button handling
- Updated translations
- Possibility to cancel exposure risk on UI request
- Updated UI


**4.7.0**

- Possibility to generate Covid Test Subscription when Exposure Notification returns HIGH RISK
- Added cache for UI requests when error should be handled by native
- Updated UI


**4.6.0**

- Added list of current color zones for poviats.
- Possibility of monitoring selected poviats - notification in case of a change in the color of the zone.
- Updated screen that informs about the possibility to add a translation into a new language.
- Updated UI


**4.5.0**

- Added closing app on UI request
- Upgraded gradle and gradle dependencies
- Added response "ACCESS_DENIED" code(5) when user denied sharing TEKs
- Updated UI


**4.4.0**

- Added translations for English and Ukrainian languages
- Updated UI


**4.3.1**

- Updated UI


**4.3.0**

- Updated app name
- Updated UI
- Added validation of Temporary Exposure Keys before upload
- Getting and setting application language/locale (not supported by UI yet)


**4.2.4**

- Updated GIS recommendations


**4.2.3**

- Updated PWA
- Passed app version to PWA
- Updated certification pinning


**4.2.2**

- Updated PWA


**4.2.1**

- Increased connection timeouts
- Handled upload errors
- Updated PWA


**4.2.0**

- PWA GUI migrated to offline version (local assets)
- Migration of PWA data from previous versions added
- Improved device verification check (missing Google Play services handled properly)
- Risk level thresholds added as configuration
- VPN connection problem fixed (still valid for keys upload)
- Improved download files process (omitting not existing files)


**4.1.1**

- Security fixes added:
    - Migration of Firebase Cloud Functions to HTTP calls
    - Certificate pinning for HTTP calls and PWA application
    - Preventing app screen recording/taking screenshots
    - Warning about rooted devices displayed on first app launch
    - In-app updates added
- Config changes


**4.1.0**

- Exposure Notification API added
- OpenTrace module fully removed together with all collected data
- New types of communication with PWA via JS Bridge


**3.0.3**

- Fixed wrong condition for battery optimization check for Android 5
- SafetyNet check disabled


**3.0.2**

- Improved Bluetooth module operation
- Added support for deleting all data collected by Bluetooth module


**3.0.1**

- Added OpenTrace module for collecting BLE contacts


**2.0.1**

- Basic version with PWA, and notifications
