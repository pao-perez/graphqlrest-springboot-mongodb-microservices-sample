#!/bin/sh

MONGO_USERNAME=$(cat /mnt/disks/${DEPLOYMENT_ENV}-contentually/secrets/mongo/username)
MONGO_PASSWORD=$(cat /mnt/disks/${DEPLOYMENT_ENV}-contentually/secrets/mongo/password)

exec java ${JAVA_OPTS} -cp "/usr/app:/usr/app/lib/*" "com.paoperez.avatarservice.MainApplication" --MONGO_USERNAME=${MONGO_USERNAME} --MONGO_PASSWORD=${MONGO_PASSWORD} ${@}