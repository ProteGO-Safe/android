# ProtegoSafe Android app

![Logo](./image.png "ProtegoSafe")

## Project structure

This is multi-module Android Studio Project. It can be compiled with gradle commands, or from Android Studio user interface.

## Project modules

- app -  all the classes related to the Android UI such as view models, adapters, views, dependency injection
- domain - in this module we place all objects that will interact with other layers
- data - contains everything related to data persistence and manipulation
- device - has everything related to Android that’s not data persistence and UI
- opentrace - OpenTrace code for bluetooth tracking

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

3. Protocol version


### Config parameters in gradle.properties

| Name                               | Description                                                  |
| ---------------------------------- | ------------------------------------------------------------ |
| ORG                                | Organisation code indicating the country and health authority with which devices with installed application will be enrolled |
| SERVICE_FOREGROUND_NOTIFICATION_ID | ID for tracing service notifications                         |
| SERVICE_FOREGROUND_CHANNEL_ID      | ID for tracing service notifications channel                 |
| SERVICE_FOREGROUND_CHANNEL_NAME    | Name of tracing service notifications channel                |
| SCAN_DURATION                      | Duration of BLE scan [ms]                                    |
| MIN_SCAN_INTERVAL                  | Minimum interval between BLE scans [ms]                      |
| MAX_SCAN_INTERVAL                  | Maximum interval between BLE scans [ms]                      |
| ADVERTISING_DURATION               | Duration of BLE advertisment [ms]                            |
| ADVERTISING_INTERVAL               | Interval between BLE advertisments [ms]                      |
| PURGE_INTERVAL                     | Interval between searching entries to purge [ms]             |
| PURGE_TTL                          | Maximum age of tracing entries in database [ms]              |
| BM_CHECK_INTERVAL                  | Interval between checks of temporary id validity [ms]        |
| HEALTH_CHECK_INTERVAL              | Interval between self checks of Bluetooth Monitoring Service [ms] |
| CONNECTION_TIMEOUT                 | Timeout for connections to other devices [ms]                |
| BLACKLIST_DURATION                 | Duration of blacklisting of contacted device [ms]            |
| FIREBASE_REGION                    | Firebase region (e.g. europe-west3)                          |
| FIREBASE_APP_KEY                   | Application ID                                               |
| STAGING_FIREBASE_UPLOAD_BUCKET     | Firebase bucket for uploading tracing files in debug type of application |
| STAGING_SERVICE_UUID               | Bluetooth service  UUIDin debug type of application          |
| V2_CHARACTERISTIC_ID               | Id of BLE characteristic                                     |
| PRODUCTION_FIREBASE_UPLOAD_BUCKET  | Firebase bucket for uploading tracing files in release type of application |
| PRODUCTION_SERVICE_UUID            | Bluetooth service  UUIDin release type of application        |
| PROD_WEB_URL                       | PWA URL - prod build flavour                                 |
| STAGE_WEB_URL                      | PWA URL - stage build flavour                                |
| DEV_WEB_URL                        | PWA URL - dev build flavour                                  |

### Firebase and google-services.json
Setup Firebase for the different environment.
Download the google-services.json for each of each environment.

Prod: ./app/src/prod/google-services.json

Stage: ./app/src/stage/google-services.json

Dev: ./app/src/dev/google-services.json

---

### Protocol Version
Protocol version used should be 2 (or above)
Version 1 of the protocol has been deprecated

---

## ChangeLog

2.0.1

Basic version with PWA, and notifications

3.0.1

Added OpenTrace module for collecting BLE contacts

3.0.2

Improved Bluetooth module operation

Added support for deleting all data collected by Bluetooth module

3.0.3

Fixed wrong condition for battery optimization check for Android 5

SafetyNet check disabled
