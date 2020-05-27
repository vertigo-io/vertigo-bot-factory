#!/bin/bash

mkdir -p /opt/backup/data
cd /opt/backup/data/

d=`date +%Y%m%d`
error=error.log

pg_dump -Fc -U postgres chatbot > backup-"$d".dump 2>$error

code=$?
if [ $code -ne 0 ]; then
  echo 1>&2 "The backup failed (exit code $code), check for errors in $error"
  exit 1
else
  ls -1tr | grep -i .dump$ | head -n -10 | xargs -d '\n' rm -f --
fi
