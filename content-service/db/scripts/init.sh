#!/bin/sh

MONGO_USERNAME=$(cat /mnt/disks/secrets/mongo_username)
MONGO_PASSWORD=$(cat /mnt/disks/secrets/mongo_password)

echo "Creating user ${MONGO_USERNAME} with readWrite access to database contentually..."
mongo admin --eval "db.createUser({ user: '${MONGO_USERNAME}', pwd: '${MONGO_PASSWORD}', roles: [{ role: 'readWrite', db: 'contentually' }]});"