#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=
ROOT_DIR=
TARGET_SPECIFIED=false

usage() {
cat << EOF
  Usage: $0 -t environment

  Setup environment for services locally.

  Options:
    -t set target environment [ required ]

  Example: $0 -t development
EOF
}

cleanup() {
  docker-compose down
  rm -rf $ROOT_DIR
}

setup_service() {
  local service=$1
  local app_dir=$(pwd)
  local buildkit_enabled=0
  
  if [[ $DEPLOYMENT_ENV == "local" ]]; then
    buildkit_enabled=1
  fi

  ROOT_DIR=/mnt/disks/$DEPLOYMENT_ENV-contentually
  # Create service log dir
  mkdir -m 777 -p $ROOT_DIR/$service/service/log
  # Build service container image
  cd $app_dir/$service-service && DOCKER_BUILDKIT=$buildkit_enabled docker build -t $service-service:0.0.1 . && cd -
  if [[ $service != "discovery" ]] && [[ $service != "graphql" ]]; then
    # Setup db data/log and secrets directories
    mkdir -p $ROOT_DIR/$service/db/data $ROOT_DIR/$service/secrets
    mkdir -m 777 -p $ROOT_DIR/$service/db/log
    # Setup Mongo db access
    cat $app_dir/secrets/mongo_username > $ROOT_DIR/$service/secrets/mongo_username
    cat $app_dir/secrets/mongo_password > $ROOT_DIR/$service/secrets/mongo_password
    # Build db container image
    cd $app_dir/$service-service/db && docker build -t $service-db:0.0.1 . && cd -
  fi
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
      setup_service avatar
      setup_service category
      setup_service content
      setup_service image
      setup_service discovery
      setup_service graphql
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

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose -f docker-compose.yaml -f docker-compose.local.yaml up

cleanup