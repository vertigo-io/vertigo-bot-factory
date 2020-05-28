#!/bin/bash

docker exec chatbotfactory_postgres_1 /opt/backup/backup.sh

docker exec chatbotfactory_influxdb_1 /opt/backup/backup.sh
