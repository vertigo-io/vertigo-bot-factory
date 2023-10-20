#!/bin/bash

_term() { 
  echo "Caught SIGTERM signal!" 
  
  su -c "/usr/local/tomcat/bin/shutdown.sh" tomcat
  
  exit 0
}

trap _term SIGTERM

# Customize tomcat deploy path from env var DEPLOY_PATH (note '//\//#' replace all '/' with '#' for tomcat context name convention)
rm -f /usr/local/tomcat/conf/Catalina/localhost/*.xml
cp /usr/local/tomcat/conf/Catalina/localhost/designer.xml.ref /usr/local/tomcat/conf/Catalina/localhost/${DEPLOY_PATH//\//#}.xml

# Start Tomcat
su -c "/usr/local/tomcat/bin/startup.sh" tomcat

touch /usr/local/tomcat/logs/catalina.out
tail -f /usr/local/tomcat/logs/catalina.out & # just to keep the process alive for docker, also, tomcat logs are visible on container logs
wait "$!" # wait command permit SIGTERM handling
