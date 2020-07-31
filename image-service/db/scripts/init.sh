#!/bin/sh

echo "Creating user ${MONGO_USERNAME} with readWrite access to database contentually..."
mongo admin --eval "db.createUser({ user: '${MONGO_USERNAME}', pwd: '${MONGO_PASSWORD}', roles: [{ role: 'readWrite', db: 'contentually' }]});"

echo "Importing image data..."
echo '{ "name": "300x200", "url": "https://placekitten.com/200/300", "alt": "Kitten at 300x200", "width": 300, "height": 200 }
      { "name": "150x150", "url": "https://placekitten.com/150/150", "alt": "Kitten at 150x150", "width": 150, "height": 150 }' | 
      mongoimport --db contentually --collection image \
      --authenticationDatabase admin --username ${MONGO_USERNAME} --password ${MONGO_PASSWORD} \
      --drop