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
logFile=logInstall_"$d".log
printf "" > $logFile # init logFile

say() {
	printf "$1\n"
	log "$1\n"
}

log() {
	printf "${1//%/%%}" | sed 's/\x1b\[[0-9;]*[A-Za-z]//g' >> $logFile 2>&1 # log without text formating (CSI sequence)
}

exec_logged() {
	cmd=$@
	log "> $cmd\n"
	out=`$cmd 2>&1`
	ret=$?
	if [[ -z $out ]]
	then
		log "\n"
	else
		log "$out\n\n" # new line at the end are stripped
	fi
	
	if [ $ret -ne 0 ]
	then
		say 'An \e[31;1merror\e[0m occured :( See log file for more info.'
		exit 1
	fi
}

numberOfStep=8
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
say "\n" # move to a new line

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
exec_logged /bin/bash ./additionalFiles/installCompose.sh

display_step
exec_logged /bin/bash ./additionalFiles/copyData.sh $installDir
exec_logged /bin/bash ./additionalFiles/buildConfig.sh $installDir $runnerCount

display_step
exec_logged docker load -i ./additionalFiles/images/analytica.tar.gz

display_step
exec_logged docker load -i ./additionalFiles/images/cf_designer.tar.gz

display_step
exec_logged docker load -i ./additionalFiles/images/cf_runner.tar.gz

display_step
exec_logged docker-compose -f $installDir/docker-compose.yml pull postgres influxdb # get from internet public images

display_step
exec_logged /bin/bash ./additionalFiles/installCron.sh $installDir

display_step
exec_logged /bin/bash $installDir/start.sh


say "\e[32mInstallation done !\e[0m"

say "Services are starting and will be soon available.\n"

printf "Here is the configuration I generated for you :\n"
cat $installDir/config.properties

printf "\nIt can be found later in ${installDir}/config.properties\n"
