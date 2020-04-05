#!/bin/bash

VENV_PATH=venv
LOCALIZATION_OUTPUT_PATH=./app/src/main/res
GSHEET_KEY=1vpJiu2jJcxBFWefIyi__QZZH6nwsIVktPFHEohJNMsQ
GSHEET_CREDENTIALS_PATH=scripts/localization_credentials.json
GSHEET_EXPORTER_SCRIPT_PATH=scripts/export.py
GSHEET_EXPORTER_REQUIREMENTS_PATH=scripts/requirements.txt

if [ ! -f $GSHEET_CREDENTIALS_PATH ]; then
   echo "Google credentials file not found at $GSHEET_CREDENTIALS_PATH."
   echo "Correct way to use: call \"./scripts/updateLocalization.sh\" from project's root directory"
   exit 1
fi

echo `pwd`

cd "$(dirname "$0")/.."
python3 -m venv $VENV_PATH
source $VENV_PATH/bin/activate
python3 -m pip install -r $GSHEET_EXPORTER_REQUIREMENTS_PATH
python3 $GSHEET_EXPORTER_SCRIPT_PATH --credentials $GSHEET_CREDENTIALS_PATH --gsheet_key $GSHEET_KEY --android_res $LOCALIZATION_OUTPUT_PATH
