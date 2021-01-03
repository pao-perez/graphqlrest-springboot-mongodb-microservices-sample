#!/bin/sh

MONGO_USERNAME=$(cat /mnt/disks/${DEPLOYMENT_ENV}-contentually/secrets/mongo/username)
MONGO_PASSWORD=$(cat /mnt/disks/${DEPLOYMENT_ENV}-contentually/secrets/mongo/password)

echo "Creating user ${MONGO_USERNAME} with readWrite access to database contentually..."
mongo admin --eval "db.createUser({ user: '${MONGO_USERNAME}', pwd: '${MONGO_PASSWORD}', roles: [{ role: 'readWrite', db: 'contentually' }]});"

mongo contentually --eval "db.Content.createIndex({ rank: 1 }, { unique: true });" --authenticationDatabase admin --username ${MONGO_USERNAME} --password ${MONGO_PASSWORD}