#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=standalone
SERVICE=avatar
ROOT_DIR=/mnt/disks/${DEPLOYMENT_ENV}-contentually/${SERVICE}

# Create spring log dir
mkdir -m 777 -p ${ROOT_DIR}/logs

# Build service container image
DOCKER_BUILDKIT=1 docker build -t $SERVICE-service:0.0.1 .

# Build db container image
cd db/ && docker build -t ${SERVICE}-db:0.0.1 . && cd -

DEPLOYMENT_ENV=${DEPLOYMENT_ENV} docker-compose up

docker-compose down
docker volume rm ${SERVICE}-service_servicelog
rm -r ${ROOT_DIR}