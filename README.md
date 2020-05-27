# ProteGO Safe Android app

![Logo](./image.png "ProtegoSafe")

## Project overview

This is an Android application for [ProteGO Safe project](https://github.com/ProteGO-Safe/specs) and it implements two main features:
* User daily triage - //TODO description to be provided or linked to main documentation//
* Contact tracing - module that is fully based on [Exposure Notification API](https://www.google.com/covid19/exposurenotifications/) provided by Google and Apple and it's goal is to inform people of potential exposure to COVID-19

Applicaiton is structured based on Clean Architecture pattern and Presentation (UI) layer is almost fully realized with a single Fragment (HomeFragment) with a WebView control that loads a website application called 'PWA'. PWA is responsible for GUI, user interaction and 'User daily triage' feature. A website app interacts with a native code through the JavaScript bridge and is described widely [here](doc/JavaScriptBridge.md). Native application business logic is implemented in a HomeViewModel and it decideds what kind of UseCase should be executed based on application state. UseCases are defined in a Domain layer and they call methods from proper repository. Repositories implementations are either in a Data module (responsible for everything that is data related - storing, fetching or manipulation) or Device module (device specific APIs not directly related to data).

App implements contact tracing module that is based on [ExposureNotification API](https://www.google.com/covid19/exposurenotifications/) (EN) and we can extract couple of features related to this:
* [Controlling EN](doc/ControllingExposureNotification.md): start, stop, check if device supports it, check what is its state
* [Uploading Temporary Exposure Keys](.doc/UploadingTemporaryExposureKeys.md) (TEKs) of positively diagnosed user ([veriefed by the application](TODO_description_of_verification_process)): authorize user for TEKs upload, get TEKs from EN, add proper verification data (using SafetyNet API), upload data to the [Cloud Server](TODO_link_to_cloud_server_documentation).
* [Downloading](.doc/DownloadingDiagnosisKeys.md) periodicaly files with batch of TEKs of positively diagnosed users (that recently uploaded their TEKs): execute periodic task responsible for downloading recently created .zip files (it fetches list of available files from CDN, selects only not yet analyzed files and downloads only these ones)
* [Providing files](.doc/ProvidingDiagnosisKeys.md) to EN API for detecting exposures: get proper configuration for risk calculation (Exposure Configuration), fire EN API with list of downloaded files and configuration, delete analyzed files
* [Receiving information](.doc/ReceivingExposuresInformation.md) about detected exposures: register broadcast receiver about exposures, get information about exposures, store part of information (day of exposure, risk score and duration that is in 5 minutes intervals but max 30 minutes)
* [Reporting risk level](.doc/ReportingRiskLevel.md) to the PWA: extract risk scores of saved exposures and calculate [risk level](TODO_link_to_risk_level_calculation_documentation), pass risk level to PWA
* [Removing historical data](.doc/RemovingHistoricalData.md): remove information about exposures older than 14 days


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
| STAGING_FIREBASE_REGION            | Firebase region in development type of application                                  |
| STAGING_FIREBASE_UPLOAD_BUCKET     | Firebase bucket for uploading data in development type of application                                 |
| STAGING_FIREBASE_STORAGE_BUCKET    | Firebase bucket for files storage in development type of application                                 |
| STAGING_SAFETYNET_API_KEY          | SafetyNet device attestation API Key in development type of application                                |
| PRODUCTION_FIREBASE_REGION         | Firebase region in production type of application                               |
| PRODUCTION_FIREBASE_UPLOAD_BUCKET  | Firebase bucket for uploading data in production type of application                          |
| PRODUCTION_FIREBASE_STORAGE_BUCKET | Firebase bucket for files storage in production type of application                                 |
| PRODUCTION_SAFETYNET_API_KEY       | SafetyNet device attestation API Key in production type of application                                    |
| PROD_WEB_URL                       | PWA URL - prod build flavour                                 |
| STAGE_WEB_URL                      | PWA URL - stage build flavour                                |
| DEV_WEB_URL                        | PWA URL - dev build flavour                                  |
| SAFETYNET_API_KEY                  | Api key for SafetyNet                                        |
| SHARED_PREFERENCES_FILE_NAME       | File name for Shared Preferences storage                     |

### Firebase and google-services.json
Setup Firebase for the different environment.
Download the google-services.json for each of environment and put into proper directory:

Prod: ./app/src/prod/google-services.json

Stage: ./app/src/stage/google-services.json

Dev: ./app/src/dev/google-services.json

---

## ChangeLog


4.1.0 (TBD)

Exposure Notification API added

OpenTrace module fully removed together with all collected data

3.0.3

Fixed wrong condition for battery optimization check for Android 5

SafetyNet check disabled

3.0.2

Improved Bluetooth module operation

Added support for deleting all data collected by Bluetooth module

3.0.1

Added OpenTrace module for collecting BLE contacts

2.0.1

Basic version with PWA, and notifications
