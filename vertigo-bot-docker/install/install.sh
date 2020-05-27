#!/bin/bash

# ###
# Utilities and log management
# ###

cd "$(dirname $(realpath $0))" # Make sure relative path are from this script file

check_program() {
  command -v "${1}" >/dev/null 2>&1
  return $?
}

d=`date +%Y_%m_%d_%H_%M_%S`
logFile=installLog_"$d".log
printf "" > $logFile # init logFile

say() {
	printf "$1\n"
	log "$1\n"
}

log() {
	printf "$1" | sed 's/\x1b\[[0-9;]*[A-Za-z]//g' >> $logFile # log without text formating (CSI sequence)
}

exec_logged() {
	`$@ >> $logFile 2>&1`
	if [ $? -ne 0 ]
	then
		say 'Unknown \e[31;1merror\e[0m. See log file for more info.'
		exit 1
	fi
}

numberOfStep=7
stepNumber=1

display_step() {
  if [ $stepNumber -eq 1 ]
  then
    say "Step $stepNumber/$numberOfStep"
  else
    say "\e[1F\e[KStep $stepNumber/$numberOfStep"
  fi
  
  if [ $stepNumber -eq $numberOfStep ]
  then
    say "" # new line after last step
  fi
  
  stepNumber=$((stepNumber+1))
}


# ###
# Check prerequisites
# ###


say "Check prerequisite...\n"

if [ "$EUID" -ne 0 ]
then
  say "Please run as root"
  exit 1
fi

if ! check_program docker
then
  say "Please install docker first"
  exit 1
fi

if ! docker info >/dev/null 2>&1
then
  say "Please ensure docker daemon is running"
  exit 1
fi

say "Prerequisite \e[32mok\e[0m\n"

# ###
# Prompt for options
# ###

say "How many runner to install ? [1-9]"
read runnerCount
while [[ $runnerCount -lt 1 || $runnerCount -gt 9 ]]; do
  echo "bad value"
  read runnerCount
done

say "Will install the factory with $runnerCount runner(s), ok ? (Y/N)"
read -n 1 -r
say "" # move to a new line

if [[ ! $REPLY =~ ^[Yy]$ ]]
then
  say "Installation aborted"
  exit 1
fi

installDir=/opt/chatbot_factory

# ###
# Start install
# ###

say "Install in progress..."
display_step

if ! check_program docker-compose
then
  log "Installing docker-compose...\n"
  curl -L "https://github.com/docker/compose/releases/download/1.25.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose >> $logFile 2>&1
  chmod +x /usr/local/bin/docker-compose >> $logFile 2>&1
  
  if ! check_program docker-compose
  then
    log "Fail to install docker compose :("
    exit 1
  fi
  
  log "Installing docker-compose : done\n"
fi

display_step
exec_logged mkdir -p $installDir
exec_logged cp ./additionalFiles/data/* $installDir/
exec_logged /bin/bash ./additionalFiles/buildConfig.sh $installDir $runnerCount

display_step
exec_logged docker load -i ./additionalFiles/images/analytica.tar.gz

display_step
exec_logged docker load -i ./additionalFiles/images/cf_designer.tar.gz

display_step
exec_logged docker load -i ./additionalFiles/images/cf_runner.tar.gz

display_step
exec_logged docker-compose -f $installDir/docker-compose.yml pull # get from internet public images

display_step
exec_logged /bin/bash $installDir/start.sh


say "\e[32mInstallation done !\e[0m"

say "Services are starting and will be soon available.\n"

printf "Here is the configuration I generated for you :\n"
cat $installDir/config.properties

printf "\nIt can be found later in $installDirconfig.properties\n"
