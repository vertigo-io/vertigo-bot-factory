#!/bin/bash

docker build -t vertigoio/bot-factory-runner -t vertigoio/bot-factory-runner:0.8.0 -f ./DockerfileRunner .
docker build -t vertigoio/bot-factory-runner-plugins -t vertigoio/bot-factory-runner-plugins:0.8.0 -f ./DockerfileRunner-plugin .
