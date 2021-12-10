#!/bin/bash

rm /usr/local/apache2/htdocs/*
cp -a /usr/local/tmp/html/. /usr/local/apache2/htdocs/
sed -i "s|RUNNER_URL|$1|g" /usr/local/apache2/htdocs/index.html
sed -i "s|RUNNER_URL|$1|g" /usr/local/apache2/htdocs/page2.html
sed -i "s|BOT_IHM_BASE_URL|$2|g" /usr/local/apache2/htdocs/index.html
sed -i "s|BOT_IHM_BASE_URL|$2|g" /usr/local/apache2/htdocs/page2.html

apachectl -D FOREGROUND