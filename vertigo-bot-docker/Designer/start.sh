#!/bin/bash

_term() { 
  echo "Caught SIGTERM signal!" 
  
  su -c "/opt/tomcat/bin/shutdown.sh" tomcat
  
  exit 0
}

trap _term SIGTERM

su -c "/opt/tomcat/bin/startup.sh" tomcat

touch /opt/tomcat/logs/catalina.out
tail -f /opt/tomcat/logs/catalina.out & # just to keep the process alive for docker, also, tomcat logs are visible on container logs
wait "$!" # wait command permit SIGTERM handling
