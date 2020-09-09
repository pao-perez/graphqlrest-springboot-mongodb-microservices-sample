#!/bin/bash

# Exit as soon as an error is encountered
set -e

# Get environment values from Metadata server
DEPLOYMENT_ENV=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/deployment-env" -H "Metadata-Flavor: Google")

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose up
