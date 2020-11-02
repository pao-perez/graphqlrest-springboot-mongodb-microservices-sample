#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=
SERVICE_SELECTED=false
TARGET_SPECIFIED=false
COMPOSE_FILE=docker-compose.local.yaml
ROOT_DIR=

usage() {
cat << EOF
  Usage: $0 -t environment [options] 

  Setup environment/s for service/s locally.

  Options:
    -t set target environment [ required ]
    -a setup avatar-service
    -c setup category-service
    -o setup content-service
    -d setup discovery-service
    -g setup graphql-service
    -i setup image-service
    -e setup all services

  Example: $0 -t development -d -i
EOF
}

append_service() {
local service=$1
cat <<EOF >> $COMPOSE_FILE
  $service-service:
    image: $service-service:0.0.1
EOF
}

append_db() {
local service=$1
cat <<EOF >> $COMPOSE_FILE
  $service-db:
    image: $service-db:0.0.1
    build:
      context: ./$service-service/db
EOF
}

cleanup() {
  rm $COMPOSE_FILE
  rm -rf $ROOT_DIR
}

quit() {
  cleanup
  usage
  exit
}

setup_service() {
  local service=$1

  if [[ $service == "discovery" ]] && [[ $DEPLOYMENT_ENV == "local" ]]; then
    echo "Client services aren't registered for discovery on 'local' environment, please use a different environment to use the discovery service"
    quit
  fi

  if [[ $service != "discovery" ]]; then
    SERVICE_SELECTED=true
  fi

  local app_dir=$(pwd)
  ROOT_DIR=/mnt/disks/$DEPLOYMENT_ENV-contentually
  # Create service log dir
  mkdir -m 777 -p $ROOT_DIR/$service/service/log
  # Append service to compose file
  append_service $service
  # Build service container image
  cd $app_dir/$service-service && docker build -t $service-service:0.0.1 . && cd -
  if [[ $service != "discovery" ]] && [[ $service != "graphql" ]]; then
    # Setup db data/log and secrets directories
    mkdir -p $ROOT_DIR/$service/db/data $ROOT_DIR/$service/secrets
    mkdir -m 777 -p $ROOT_DIR/$service/db/log
    # Setup Mongo db access
    cat $app_dir/secrets/mongo_username > $ROOT_DIR/$service/secrets/mongo_username
    cat $app_dir/secrets/mongo_password > $ROOT_DIR/$service/secrets/mongo_password
    # Append db to compose file
    append_db $service
    # Build db container image
    cd $app_dir/$service-service/db && docker build -t $service-db:0.0.1 . && cd -
  fi
}

# Initialize compose file
cat <<EOF > $COMPOSE_FILE
version: '3.7'

services:
EOF

while getopts "t:acodgie" opt; do
  case $opt in
    t)
      # Preceeding services will have invalid target environment if this isn't specified first
      if [[ $OPTIND -ne 3 ]]; then
        echo "Target environment (-t) needs to be specified as first argument"
        quit
      fi
      if [[ ! $OPTARG =~ ^[a-zA-Z]+$ ]]; then
        echo "Invalid argument $OPTARG, only letter/s are allowed for the target environment (-t)"
        quit
      fi
      DEPLOYMENT_ENV=$OPTARG
      TARGET_SPECIFIED=true
      ;;
    a)
      setup_service avatar
      ;;
    c)
      setup_service category
      ;;
    o)
      setup_service content
      ;;
    d)
      setup_service discovery
      ;;
    g)
      setup_service graphql
      ;;
    i)
      setup_service image
      ;;
    e)
      setup_service discovery
      setup_service avatar
      setup_service category
      setup_service content
      setup_service graphql
      setup_service image
      ;;
    ?)
      quit
      ;;
  esac
done

if [[ $SERVICE_SELECTED = false ]]; then
  echo "Atleast (1) service needs to be specified"
  quit
fi

if [[ $TARGET_SPECIFIED = false ]]; then
  echo "Target environment (-t) needs to be specified"
  quit
fi

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose -f docker-compose.yaml -f docker-compose.discovery.yaml -f $COMPOSE_FILE up

cleanup
