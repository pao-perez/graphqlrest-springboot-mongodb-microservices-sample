#!/bin/sh

echo "Creating user ${MONGO_USERNAME} with readWrite access to database contentually..."
mongo admin --eval "db.createUser({ user: '${MONGO_USERNAME}', pwd: '${MONGO_PASSWORD}', roles: [{ role: 'readWrite', db: 'contentually' }]});"

echo "Importing category data..."
echo '{ "name": "Tutorial" }
      { "name": "Blog" }' | 
      mongoimport --db contentually --collection category \
      --authenticationDatabase admin --username ${MONGO_USERNAME} --password ${MONGO_PASSWORD} \
      --drop