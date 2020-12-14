#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=standalone
SERVICE=avatar

# Build service container image
docker build -t $SERVICE-service:0.0.1 .

# Build db container image
cd db/ && docker build -t $SERVICE-db:0.0.1 . && cd -

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose up

docker-compose down
docker volume rm ${SERVICE}-service_servicelog