#!/bin/bash

# Exit as soon as an error is encountered
set -e
# Enable debug mode/printing of all executed commands
set -x

DEPLOYMENT_ENV=$1
PROJECT_NUMBER=$2
PROJECT_ID=$3
ZONE=asia-southeast1-b
DISK_AUTO_DELETE=yes
# DISK_NAME=$DEPLOYMENT_ENV-contentually-data
# INSTANCE_NAME=$DEPLOYMENT_ENV-contentually

if [[ $DEPLOYMENT_ENV == "" ]]; then
    echo "DEPLOYMENT_ENV is invalid. Exiting setup script."
    exit 1;
fi

gcloud compute --project=$PROJECT_ID disks create $DEPLOYMENT_ENV-contentually-data \
    --zone=$ZONE \
    --size=10 \
    --type=pd-standard

if [[ $DEPLOYMENT_ENV == "production" ]]; then
    DISK_AUTO_DELETE=no
fi

gcloud compute --project=$PROJECT_ID instances create $DEPLOYMENT_ENV-contentually \
    --zone=$ZONE \
    --machine-type=g1-small \
    --subnet=default \
    --network-tier=PREMIUM \
    --metadata-from-file=startup-script=setup-startup-script.sh \
    --metadata=deployment-env=$DEPLOYMENT_ENV \
    --maintenance-policy=MIGRATE \
    --service-account=$PROJECT_NUMBER-compute@developer.gserviceaccount.com \
    --scopes=cloud-platform \
    --tags=http-server,https-server \
    --image=ubuntu-minimal-1804-bionic-v20200923 \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=10GB \
    --boot-disk-type=pd-standard \
    --boot-disk-device-name=$DEPLOYMENT_ENV-contentually-boot \
    --disk=name=$DEPLOYMENT_ENV-contentually-data,device-name=$DEPLOYMENT_ENV-contentually-data,mode=rw,boot=no,auto-delete=$DISK_AUTO_DELETE \
    --shielded-secure-boot \
    --shielded-vtpm \
    --shielded-integrity-monitoring \
    --reservation-affinity=any

gcloud compute --project=$PROJECT_ID scp ./docker-compose.yaml root@$DEPLOYMENT_ENV-contentually:/

gcloud compute --project=$PROJECT_ID firewall-rules create default-allow-http-services \
    --direction=INGRESS \
    --priority=1000 \
    --network=default \
    --action=ALLOW \
    --rules=tcp:8080-8084,tcp:8761 \
    --source-ranges=0.0.0.0/0 \
    --target-tags=http-server \
    --description='Allow incoming traffic on TCP port 8080-8084,8761'
