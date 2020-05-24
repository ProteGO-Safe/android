# ProteGO Safe Android app

![Logo](./image.png "ProtegoSafe")

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

### Firebase and google-services.json
Setup Firebase for the different environment.
Download the google-services.json for each of environment and put into proper directory:

Prod: ./app/src/prod/google-services.json

Stage: ./app/src/stage/google-services.json

Dev: ./app/src/dev/google-services.json

---

## ChangeLog

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