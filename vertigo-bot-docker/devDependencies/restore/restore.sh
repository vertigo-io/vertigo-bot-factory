#!/bin/bash
filename=${filename:-dump.dump}
user=${user:-chatbot}
database=${database:-chatbot}
while [ $# -gt 0 ]; do

   if [[ $1 == *"--"* ]]; then
        param="${1/--/}"
        declare $param="$2"
   fi

  shift
done
psql -d $database -U $user -c 'drop schema public cascade' -c 'create schema public'
pg_restore $filename -d $database -U $user
psql -d $database -U $user -a -f anonymize.sql