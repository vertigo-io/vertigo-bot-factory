#!/bin/bash

cd "$(dirname $(realpath $0))" # Make sure relative path are from this script file

docker-compose -p chatbotfactory up -d --remove-orphans