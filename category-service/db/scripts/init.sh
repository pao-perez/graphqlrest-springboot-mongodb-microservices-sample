#!/bin/sh

echo "Creating user ${MONGO_USERNAME} with readWrite access to database ${MONGO_DATABASE}."
mongo admin --host localhost --port 27017 --eval "db.createUser({user: '${MONGO_USERNAME}', pwd: '${MONGO_PASSWORD}', roles: [{ role: 'readWrite', db: '${MONGO_DATABASE}' }]});"
# "${mongo[@]}" "admin" <<-EOJS
# 	db.createUser({
# 		user: $(_js_escape ${MONGO_USERNAME}),
# 		pwd: $(_js_escape ${MONGO_PASSWORD}),
# 		roles: [ { role: 'readWrite', db: '$MONGO_DATABASE' } ]
# 	})
# EOJS
echo "User ${MONGO_USERNAME} with ReadWrite access to ${MONGO_DATABASE} database created."