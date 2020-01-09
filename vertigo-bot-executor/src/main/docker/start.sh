#!/bin/bash

_term() { 
  echo "Caught SIGTERM signal!" 
  service rabbitmq-server stop
  su -c "/opt/tomcat/bin/shutdown.sh" tomcat
  
  #tomcatPID=`ps -ef | grep java | grep tomcat | awk ' { print $2 } '`
  #wait "$tomcatPID" # cant wait as non child PID
  
  sleep 3 # workaround, a best approch is a loop to wait for pid exit
  
  killall tail
  exit 0
}

trap _term SIGTERM

service rabbitmq-server start
su -c "/opt/tomcat/bin/startup.sh" tomcat

touch /opt/tomcat/logs/catalina.out
tail -f /opt/tomcat/logs/catalina.out & # just to keep the process alive for docker, also, tomcat logs are visible on container logs
wait "$!" # wait command permit SIGTERM handling
