#!/bin/bash

docker exec -it chatbotfactory_postgres_1 /opt/backup/restore.sh

docker exec chatbotfactory_influxdb_1 /opt/backup/restore.sh