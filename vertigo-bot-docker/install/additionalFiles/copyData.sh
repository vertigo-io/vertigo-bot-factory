#!/bin/bash

installDir=$1

mkdir -p $installDir
cp -R ./additionalFiles/data/* $installDir/
find $installDir/ -name "*.sh" -exec chmod ug+x {} \;

