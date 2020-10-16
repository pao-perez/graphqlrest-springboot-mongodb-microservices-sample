#!/bin/bash

# Exit as soon as an error is encountered
set -e
# Enable debug mode/printing of all executed commands
set -x

DEPLOYMENT_ENV=$1
PROJECT_ID=$2
PROJECT_NUMBER=$3
REGION=asia-southeast1
ZONE=asia-southeast1-b
DISK_AUTO_DELETE=yes
ENV_NAME=$DEPLOYMENT_ENV-contentually
BUCKET_NAME=$PROJECT_ID-$ENV_NAME-bucket
DISK_NAME=$ENV_NAME-data
INSTANCE_NAME=$ENV_NAME-instance

if [[ $DEPLOYMENT_ENV == "" ]]; then
    echo "DEPLOYMENT_ENV is invalid. Exiting setup script."
    exit 1;
fi

if [[ $PROJECT_ID == "" ]]; then
    echo "PROJECT_ID is invalid. Exiting setup script."
    exit 1;
fi

if [[ $PROJECT_NUMBER == "" ]]; then
    echo "PROJECT_NUMBER is invalid. Exiting setup script."
    exit 1;
fi

gsutil mb -p $PROJECT_ID -l $REGION gs://$BUCKET_NAME/
gsutil cp ./docker-compose.yaml gs://$BUCKET_NAME

gcloud compute --project=$PROJECT_ID disks create $DISK_NAME \
    --zone=$ZONE \
    --size=10 \
    --type=pd-standard

if [[ $DEPLOYMENT_ENV == "production" ]]; then
    DISK_AUTO_DELETE=no
fi

gcloud compute --project=$PROJECT_ID instances create $INSTANCE_NAME \
    --zone=$ZONE \
    --machine-type=n1-standard-1 \
    --subnet=default \
    --network-tier=PREMIUM \
    --metadata-from-file=startup-script=setup-startup-script.sh \
    --metadata=deployment-env=$DEPLOYMENT_ENV \
    --maintenance-policy=MIGRATE \
    --service-account=$PROJECT_NUMBER-compute@developer.gserviceaccount.com \
    --scopes=cloud-platform \
    --tags=http-server,https-server \
    --image=ubuntu-minimal-1804-bionic-v20201014 \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=10GB \
    --boot-disk-type=pd-standard \
    --boot-disk-device-name=$INSTANCE_NAME-boot \
    --disk=name=$DISK_NAME,device-name=$DISK_NAME,mode=rw,boot=no,auto-delete=$DISK_AUTO_DELETE \
    --shielded-secure-boot \
    --shielded-vtpm \
    --shielded-integrity-monitoring \
    --reservation-affinity=any

gcloud compute --project=$PROJECT_ID firewall-rules create default-allow-http-services \
    --direction=INGRESS \
    --priority=1000 \
    --network=default \
    --action=ALLOW \
    --rules=tcp:8080-8084,tcp:8761 \
    --source-ranges=0.0.0.0/0 \
    --target-tags=http-server \
    --description='Allow incoming traffic on TCP port 8080-8084,8761'
