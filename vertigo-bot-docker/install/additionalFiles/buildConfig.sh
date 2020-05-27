#!/bin/bash

# ###
# Init
# ###

generate_pass() {
  < /dev/urandom tr -dc A-Za-z0-9_- | head -c${1:-32}
}


if [[ $# -ne 2 || $2 -lt 1 || $2 -gt 9 ]]
then
  echo "Invalid argument"
  exit 1
fi

installDir=$1
runnerCount=$2

# ###
# Generate config file
# ###

POSTGRES_PASSWORD=$(generate_pass)
echo "POSTGRES_PASSWORD=$POSTGRES_PASSWORD" > $installDir/config.properties

echo "RUNNER_COUNT=$runnerCount" >> $installDir/config.properties

for i in $(seq 1 $runnerCount)
do
  port=818${i}
  apiKey=$(generate_pass)
  eval RUNNER${i}_PORT=$port
  eval RUNNER${i}_API_KEY=$apiKey
  echo "RUNNER${i}_PORT=$port" >> $installDir/config.properties
  echo "RUNNER${i}_API_KEY=$apiKey" >> $installDir/config.properties
done

# ###
# Generate compose file
# ###

cat <<EOT > $installDir/docker-compose.yml
version: '3.5'

services:
  postgres:
    image: postgres:12.1
    environment:
      PGDATA:/var/lib/postgresql/data/pgdata
      POSTGRES_PASSWORD:$POSTGRES_PASSWORD
    volumes:
      - cf_postgres_data:/var/lib/postgresql/data/pgdata
      - ./initSql/:/docker-entrypoint-initdb.d/
      - ./backup/postgres:/opt/backup
#    ports:
#      - "5432:5432" 
    networks:
      - cb_factory
    restart: unless-stopped
  
  influxdb:
    image: influxdb:1.7.8
    volumes:
      - cf_influxdb_data:/var/lib/influxdb
      - ./backup/influxdb:/opt/backup
#    ports: 
#      - "8086:8086"
    networks:
      - cb_factory
    restart: unless-stopped

  analytica:
    image: analytica:latest
    environment:
      - INFLUXDB_URL=http://influxdb:8086
    depends_on:
      - influxdb
#    ports:
#      - "4562:4562"
    networks:
      - cb_factory
    restart: unless-stopped
  
  designer:
    image: 'cf_designer:latest'
    environment:
      - JAVA_OPTS=-Xmx512m -Xms512m
      - DB_URL=//postgres:5432/chatbot
      - ANALYTICA_HOST=analytica
      - ANALYTICA_PORT=4562
      - INFLUXDB_URL=http://influxdb:8086
      - ANALYTICA_DBNAME=chatbot
#      - devMode=true
    depends_on:
      - postgres
      - influxdb
      - analytica
    ports:
      - '8080:8080'
    networks:
      - cb_factory
    restart: unless-stopped
	
EOT

for i in $(seq 1 $runnerCount)
do
  portName=RUNNER${i}_PORT
  apiKeyName=RUNNER${i}_API_KEY
  
  cat <<EOT >> $installDir/docker-compose.yml
  runner${i}:
    image: 'cf_runner:latest'
    environment:
      - DESIGNER_URL=http://designer:8080/designer/
      - ANALYTICA_HOST=analytica
      - ANALYTICA_PORT=4562
      - ANALYTICA_DBNAME=chatbot
      - API_KEY=${!apiKeyName}
    depends_on:
      - influxdb
      - analytica
    ports:
      - '${!portName}:8080'
    volumes:
      - cf_runner_${i}_data:/opt/data
    networks:
      - cb_factory
    restart: unless-stopped

EOT
done

cat <<EOT >> $installDir/docker-compose.yml
volumes:
  cf_postgres_data:
  cf_influxdb_data:
EOT

for i in $(seq 1 $runnerCount)
do
  echo "  cf_runner_${i}_data:" >> $installDir/docker-compose.yml
done

cat <<EOT >> $installDir/docker-compose.yml

networks:
  cb_factory:

EOT