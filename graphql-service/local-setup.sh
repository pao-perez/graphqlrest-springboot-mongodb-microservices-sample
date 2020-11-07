#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=local
ROOT_DIR=/mnt/disks/$DEPLOYMENT_ENV-contentually
SERVICE=graphql

# Create service log dir
mkdir -m 777 -p $ROOT_DIR/$SERVICE/service/log
# Build service container image
docker build -t $SERVICE-service:0.0.1 .

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose up
