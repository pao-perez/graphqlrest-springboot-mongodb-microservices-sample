#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=standalone
SERVICE=graphql
APP_LOGS_DIR=/mnt/disks/${DEPLOYMENT_ENV}-contentually/spring/logs

# Create spring log dir
mkdir -m 777 -p ${APP_LOGS_DIR}

# Build service container image
DOCKER_BUILDKIT=1 docker build -t $SERVICE-service:0.0.1 .

DEPLOYMENT_ENV=${DEPLOYMENT_ENV} docker-compose up

docker-compose down
docker volume rm ${SERVICE}-service_servicelog
rm -r ${APP_LOGS_DIR}