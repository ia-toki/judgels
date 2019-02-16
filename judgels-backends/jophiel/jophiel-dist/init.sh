#!/bin/bash

set -ex

COMMAND=$1

if [[ -z ${COMMAND} ]]; then
	COMMAND="console"
fi

if [[ "$COMMAND" == "dbMigrate" ]]; then
    exec ./service/bin/jophiel db migrate var/conf/jophiel.yml
else 
    exec ./service/bin/init.sh $COMMAND
fi
