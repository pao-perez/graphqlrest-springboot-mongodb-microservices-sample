#!/bin/bash

# Exit as soon as an error is encountered
set -e

SERVICE=
DEPLOYMENT_ENV=standalone
TARGET_SPECIFIED=false

usage() {
cat << EOF
  Usage: $0 -s service

  Setup service as standalone.

  Options:
    -s set target service [ required ]

  Example: $0 -s avatar
EOF
}

while getopts "s:" opt; do
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
cd ${SERVICE}-service && DOCKER_BUILDKIT=1 docker build -t ${SERVICE}-service:0.0.1 .
if [[ $SERVICE != "discovery" ]] && [[ $SERVICE != "graphql" ]]; then
  # Build db container image
  cd db/ && docker build -t ${SERVICE}-db:0.0.1 . && cd -
fi

DEPLOYMENT_ENV=$DEPLOYMENT_ENV docker-compose up

docker-compose down
docker volume prune --force
docker volume rm ${SERVICE}-service_service-log
docker system prune --force