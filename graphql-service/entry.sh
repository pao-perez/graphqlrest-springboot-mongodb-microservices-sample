#!/bin/sh

exec java ${JAVA_OPTS} -cp "/usr/app:/usr/app/lib/*" "com.paoperez.graphqlservice.MainApplication" ${@}