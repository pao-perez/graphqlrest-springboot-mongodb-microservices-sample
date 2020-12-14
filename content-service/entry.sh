#!/bin/sh

MONGO_USERNAME=$(cat /mnt/disks/secrets/mongo_username)
MONGO_PASSWORD=$(cat /mnt/disks/secrets/mongo_password)

exec java ${JAVA_OPTS} -cp "/usr/app:/usr/app/lib/*" "com.paoperez.contentservice.MainApplication" --MONGO_USERNAME=${MONGO_USERNAME} --MONGO_PASSWORD=${MONGO_PASSWORD} ${@}