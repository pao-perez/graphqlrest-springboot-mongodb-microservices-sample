#!/bin/sh

MONGO_USERNAME=$(cat /data/secrets/mongo_username)
MONGO_PASSWORD=$(cat /data/secrets/mongo_password)

exec java ${JAVA_OPTS} -cp "/usr/app:/usr/app/lib/*" "com.paoperez.categoryservice.MainApplication" --MONGO_USERNAME=${MONGO_USERNAME} --MONGO_PASSWORD=${MONGO_PASSWORD} ${@}