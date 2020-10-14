#!/bin/bash

# Exit as soon as an error is encountered
set -e
# Enable debug mode/printing of all executed commands
set -x

DEPLOYMENT_ENV=$1
PROJECT_ID=$2

if [[ $DEPLOYMENT_ENV == "" ]]; then
    echo "DEPLOYMENT_ENV is invalid. Exiting deployment script."
    exit 1;
fi

if [[ $PROJECT_ID == "" ]]; then
    echo "PROJECT_ID is invalid. Exiting setup script."
    exit 1;
fi

gcloud compute --project=$PROJECT_ID instances add-metadata $DEPLOYMENT_ENV-contentually \
  --metadata-from-file=startup-script=deploy-startup-script.sh

gcloud compute --project=$PROJECT_ID instances stop $DEPLOYMENT_ENV-contentually

gcloud compute --project=$PROJECT_ID instances start $DEPLOYMENT_ENV-contentually
