#!/bin/bash

installDir=$1

printf "0 3 * * * root $installDir/backup.sh\n" > /etc/cron.d/chatbot_factory.cron

