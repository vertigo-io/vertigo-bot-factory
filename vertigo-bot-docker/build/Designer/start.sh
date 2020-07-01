#!/bin/bash

_term() { 
  echo "Caught SIGTERM signal!" 
  
  su -c "/opt/tomcat/bin/shutdown.sh" tomcat
  
  exit 0
}

trap _term SIGTERM

# Customize tomcat deploy path from env var DEPLOY_PATH
rm -f /opt/tomcat/conf/Catalina/localhost/*.xml
cp /opt/tomcat/conf/Catalina/localhost/designer.xml.ref /opt/tomcat/conf/Catalina/localhost/${DEPLOY_PATH//\//#}.xml

# Start Tomcat
su -c "/opt/tomcat/bin/startup.sh" tomcat

touch /opt/tomcat/logs/catalina.out
tail -f /opt/tomcat/logs/catalina.out & # just to keep the process alive for docker, also, tomcat logs are visible on container logs
wait "$!" # wait command permit SIGTERM handling
