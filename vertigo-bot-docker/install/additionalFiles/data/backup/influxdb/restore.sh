#!/bin/bash

# restore the file as argument or the newest .tar.gz file in /opt/backup/data/ if no argument
if [ $# -eq 0 ]
then
  fileName=`ls -1t /opt/backup/data/ | grep -i .tar.gz$ | head -n 1`
  fullFileName=/opt/backup/data/$fileName
else
  fullFileName=`realpath $1`
  if [ ! -f "$fullFileName" ]
  then
    echo "$fullFileName is not a file"
	exit 1
  fi
fi

# restore database after prompt
read -p "Restoring $fullFileName, ok (Y/N) ? " -n 1 -r
echo    # move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
  cd /opt/backup/data/
  rm -rf ./backup
  
  tar -xzf $fullFileName
  
  influx -execute 'DROP DATABASE chatbot' > ./restore.log
  influxd restore -portable ./backup >> ./restore.log 2>&1
  
  rm -rf ./backup
else
  echo "## ABORTED ##"
fi
