#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=
TARGET_SPECIFIED=false
APP_DIR=$PWD

usage() {
cat << EOF
  Usage: $0 -t environment

  Setup environment for services locally.

  Options:
    -t set target environment [ required ]

  Example: $0 -t development
EOF
}

setup_service() {
  local service=$1
  local buildkit_enabled=0
  
  if [[ $DEPLOYMENT_ENV == "local" ]]; then
    buildkit_enabled=1
  fi

  # Build service container image
  cd $service-service/ && DOCKER_BUILDKIT=$buildkit_enabled docker build -t $service-service:0.0.1 .
  if [[ $service != "discovery" ]] && [[ $service != "graphql" ]]; then
    # Build db container image
    cd db/ && docker build -t $service-db:0.0.1 .
  fi
  cd $APP_DIR
}

while getopts "t:" opt; do
  case $opt in
    t)
      if [[ ! $OPTARG =~ ^[a-zA-Z]+$ ]]; then
        echo "Invalid argument $OPTARG, only letter/s are allowed for the target environment (-t)"
        usage
        exit
      fi
      DEPLOYMENT_ENV=$OPTARG
      TARGET_SPECIFIED=true
      if [[ $DEPLOYMENT_ENV == "standalone" ]]; then
        echo "Error: Client services aren't registered for discovery on 'standalone' environment, please specify a different environment."
        exit
      fi
      ;;
    ?)
      usage
      exit
      ;;
  esac
done

if [[ $TARGET_SPECIFIED = false ]]; then
  echo "Target environment (-t) needs to be specified as first argument"
  usage
  exit
fi

setup_service avatar
setup_service category
setup_service content
setup_service image
setup_service discovery
setup_service graphql

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose -f docker-compose.yaml -f docker-compose.local.yaml up

docker-compose down
docker volume prune --force
docker volume rm playground-portfolio-server_service-log