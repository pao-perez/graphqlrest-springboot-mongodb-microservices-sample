#!/bin/bash

# Exit as soon as an error is encountered
set -e

# Get environment values from Metadata server
PROJECT_ID=$(curl -s "http://metadata.google.internal/computeMetadata/v1/project/project-id" -H "Metadata-Flavor: Google")
DEPLOYMENT_ENV=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/deployment-env" -H "Metadata-Flavor: Google")

RESOURCE_TAG=${DEPLOYMENT_ENV}-contentually
INSTANCE=${RESOURCE_TAG}-instance
ZONE=asia-southeast1-a
COMPOSE_FILE=docker-compose.yaml
ROOT_DIR=/mnt/disks/${RESOURCE_TAG}

# Setup for Docker
apt-get update
apt-get -y install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
# Add Docker’s official GPG key:
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
# Try to verify Docker key's fingerprint
DOCKER_KEY=$(apt-key fingerprint 0EBFCD88)
if [[ $DOCKER_KEY != *"9DC8 5822 9FC7 DD38 854A  E2D8 8D81 803C 0EBF CD88"* ]]; then
    echo "Fingerprint did not match! Exiting startup script."
    exit 1
fi
# Install Docker
add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
apt-get update
apt-get -y install docker-ce docker-ce-cli containerd.io
# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/download/1.26.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Configure Docker to authenticate with GCR
gcloud auth configure-docker --quiet

# Format and mount persistent disk
mkfs.ext4 -m 0 -E lazy_itable_init=0,lazy_journal_init=0,discard /dev/sdb
mkdir -p $ROOT_DIR
mount -o discard,defaults /dev/sdb $ROOT_DIR
chmod a+w $ROOT_DIR
cp /etc/fstab /etc/fstab.backup
echo UUID=`blkid -s UUID -o value /dev/sdb` $ROOT_DIR ext4 discard,defaults,nofail 0 2 | tee -a /etc/fstab

# Create Spring log dir
mkdir -m 777 -p ${ROOT_DIR}/logs/spring
# Create secrets dir
mkdir -p ${ROOT_DIR}/secrets/mongo

# Setup Mongo db access
echo $(gcloud secrets versions access latest --secret=mongo-username --project=$PROJECT_ID) | tee ${ROOT_DIR}/secrets/mongo/username 1> /dev/null
echo $(gcloud secrets versions access latest --secret=mongo-password --project=$PROJECT_ID) | tee ${ROOT_DIR}/secrets/mongo/password 1> /dev/null

# Download docker compose files
gsutil cp gs://${RESOURCE_TAG}-bucket/${COMPOSE_FILE} /.

# Start app
DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose -f $COMPOSE_FILE up

# Remove startup script so succeeding boot won't run this setup script
gcloud compute --project=$PROJECT_ID instances remove-metadata $INSTANCE --keys=startup-script --zone=$ZONE
