#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=standalone
ROOT_DIR=/mnt/disks/$DEPLOYMENT_ENV-contentually
SERVICE=image
APP_DIR=./..

# Create service log dir
mkdir -m 777 -p $ROOT_DIR/$SERVICE/service/log
# Build service container image
docker build -t $SERVICE-service:0.0.1 .
# Setup db data/log and secrets directories
mkdir -p $ROOT_DIR/$SERVICE/db/data $ROOT_DIR/$SERVICE/secrets
mkdir -m 777 -p $ROOT_DIR/$SERVICE/db/log
# Setup Mongo db access
cat $APP_DIR/secrets/mongo_username > $ROOT_DIR/$SERVICE/secrets/mongo_username
cat $APP_DIR/secrets/mongo_password > $ROOT_DIR/$SERVICE/secrets/mongo_password
# Build db container image
cd db/ && DOCKER_BUILDKIT=1 docker build -t $SERVICE-db:0.0.1 . && cd -

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose up

docker-compose down