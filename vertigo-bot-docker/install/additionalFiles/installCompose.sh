#!/bin/bash

check_program() {
  command -v "${1}" >/dev/null 2>&1
  return $?
}


if ! check_program docker-compose
then
  printf "Installing docker-compose...\n"
  curl -L "https://github.com/docker/compose/releases/download/1.25.5/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  chmod +x /usr/local/bin/docker-compose
  
  if ! check_program docker-compose
  then
    printf "Fail to install docker compose :(\n"
    exit 1
  fi
  
  printf "Installing docker-compose : done\n"
else
  printf "docker-compose already here\n"
fi
