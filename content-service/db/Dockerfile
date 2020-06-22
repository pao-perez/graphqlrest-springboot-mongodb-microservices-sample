FROM mongo:4.4-rc-bionic

ARG INIT_SCRIPT="./scripts/init.sh"
ARG CONFIG_FILE="./config/mongod.conf"

COPY ${INIT_SCRIPT} /docker-entrypoint-initdb.d
COPY ${CONFIG_FILE} /etc/mongod.conf
EXPOSE 27017

CMD ["mongod", "--config", "/etc/mongod.conf"]