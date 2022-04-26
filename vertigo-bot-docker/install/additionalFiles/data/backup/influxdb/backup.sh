#!/bin/bash

mkdir -p /opt/backup/data
cd /opt/backup/data/

d=`date +%Y%m%d`
log=backup.log

rm -rf ./backup
mkdir ./backup

influx backup ./backup/ -t ${DOCKER_INFLUXDB_INIT_ADMIN_TOKEN} >./$log 2>&1

code=$?
if [ $code -ne 0 ]; then
  mv ./$log ./error-$d.log
  rm -rf ./backup
  
  echo 1>&2 "The backup failed (exit code $code), check for errors in $log"
  exit 1
else
  mv ./$log ./backup/
  tar -czf $d.tar.gz ./backup
  
  rm -rf ./backup
  ls -1tr | grep -i .tar.gz$ | head -n -10 | xargs -d '\n' rm -f --
fi
