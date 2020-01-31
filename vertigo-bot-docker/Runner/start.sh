#!/bin/bash

_term() { 
  echo "Caught SIGTERM signal!" 
  
  su -c "/opt/tomcat/bin/shutdown.sh" tomcat
  service rabbitmq-server stop
  
  exit 0
}

trap _term SIGTERM

service rabbitmq-server start

while ! nc -z 127.0.0.1 5672;
do
  echo waiting rabbitmq to start;
  sleep 1;
done;
echo rabbitmq ok;

su -c "/opt/tomcat/bin/startup.sh" tomcat

touch /opt/tomcat/logs/catalina.out
tail -f /opt/tomcat/logs/catalina.out & # just to keep the process alive for docker, also, tomcat logs are visible on container logs
wait "$!" # wait command permit SIGTERM handling
