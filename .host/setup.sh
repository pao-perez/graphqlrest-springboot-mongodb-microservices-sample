#!/bin/bash

# Exit as soon as an error is encountered
set -e

DEPLOYMENT_ENV=
ROOT_DIR=
TARGET_SPECIFIED=false

usage() {
cat << EOF
  Usage: $0 -t environment

  Sets up target environment host directory.

  Options:
    -t set target environment [ required ]

  Example: $0 -t development
EOF
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
      ROOT_DIR=/mnt/disks/${DEPLOYMENT_ENV}-contentually
      TARGET_SPECIFIED=true
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

# Create spring log dir
mkdir -m 777 -p ${ROOT_DIR}/logs/spring
# Create secrets dir
mkdir -p ${ROOT_DIR}/secrets/mongo
echo "superuser" > ${ROOT_DIR}/secrets/mongo/username
echo "supersecret" > ${ROOT_DIR}/secrets/mongo/password