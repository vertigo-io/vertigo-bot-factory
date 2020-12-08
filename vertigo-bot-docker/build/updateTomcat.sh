#!/bin/bash

# check if multiple Tomcat folder (ambiguous)
for d in apache-tomcat-*; do
	if [ -d "$d" ]
	then
		if [ ! -z "$OLD_TOMCAT_DIR" ]
		then
			echo "Multiple Tomcat directory found (at least $OLD_TOMCAT_DIR and $d)"
			exit 1
		fi
		
		OLD_TOMCAT_DIR=$d
	fi
done

# Find new Tomcat achive file
for f in apache-tomcat-*.zip apache-tomcat-*.tar.gz; do
	if [ -e "$f" ]
	then
		if [ ! -z "$ARCHIVE_FILE" ]
		then
			echo "Multiple Tomcat archive file found (at least $ARCHIVE_FILE and $f)"
			exit 1
		fi
	
		ARCHIVE_FILE=$f
	fi
done

if [ -z "$ARCHIVE_FILE" ]
then
	echo "No Tomcat archive file found, please copy it in this folder (zip or tar.gz)"
	exit 1
fi

read -p "Using '$ARCHIVE_FILE', ok ? " -n 1 -r
echo    # (optional) move to a new line
if [[ ! $REPLY =~ ^[YyOo]$ ]]
then
	echo "Aborted"
    exit 2
fi

#
echo "$OLD_TOMCAT_DIR"
if [ ! -z "$OLD_TOMCAT_DIR" ]
then
	echo "Removing previous Tomcat..."
	rm -rf "$OLD_TOMCAT_DIR"
fi

#
echo "Unzipping..."

if [[ "$ARCHIVE_FILE" == *.zip ]]
then
	unzip -q "$ARCHIVE_FILE"
else
	tar -xf "$ARCHIVE_FILE"
fi

#
echo "Configuring Tomcat..."

sed -i 's/<Connector port="8080"/<Connector maxPostSize="209715200"\n               port="8080"/' apache-tomcat-*/conf/server.xml

rm -rf apache-tomcat-*/webapps/*

#
echo "Cleaning..."

rm -f "$ARCHIVE_FILE"

echo "DONE !"