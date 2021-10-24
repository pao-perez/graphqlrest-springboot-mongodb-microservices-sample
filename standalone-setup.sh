#!/bin/bash

# Exit as soon as an error is encountered
set -e

SERVICE=
DEPLOYMENT_ENV=standalone
TARGET_SPECIFIED=false
DEBUG_ENABLED=false

usage() {
cat << EOF
  Usage: $0 -s service

  Setup service as standalone.

  Options:
    -s set target service [ required ]
    -d enable debug mode

  Example: $0 -s avatar -d
EOF
}

while getopts "s:d" opt; do
  case $opt in
    s)
      if [[ $OPTARG != "discovery" ]] && [[ $OPTARG != "graphql" ]] && [[ $OPTARG != "avatar" ]] && [[ $OPTARG != "category" ]] && [[ $OPTARG != "content" ]] && [[ $OPTARG != "image" ]]; then
        echo "Invalid argument $OPTARG, only the following services are available: discovery, graphql, avatar, category, content, image"
        usage
        exit
      fi
      SERVICE=$OPTARG
      TARGET_SPECIFIED=true
      ;;
    d)
      echo "Debug enabled, disabling Buildkit.."
      DEBUG_ENABLED=true
      ;;
    ?)
      usage
      exit
      ;;
  esac
done

if [[ $TARGET_SPECIFIED = false ]]; then
  echo "Target service (-s) needs to be specified as first argument"
  usage
  exit
fi

# Build service container image
DOCKER_BUILDKIT=0
if [[ $DEBUG_ENABLED == false ]]; then
  DOCKER_BUILDKIT=1
fi
cd ${SERVICE}-service && DOCKER_BUILDKIT=$DOCKER_BUILDKIT docker build -t ${SERVICE}-service:0.0.1 .
if [[ $SERVICE != "discovery" ]] && [[ $SERVICE != "graphql" ]]; then
  # Build db container image
  cd db/ && docker build -t ${SERVICE}-db:0.0.1 . && cd -
fi

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose up

docker-compose down
docker volume prune --force
docker volume rm ${SERVICE}-service_service-log
docker system prune --force