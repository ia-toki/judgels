#!/bin/bash

set -ex

COMMAND=$1

if [[ -z ${COMMAND} ]]; then
	COMMAND="console"
fi

if [[ "$COMMAND" == "dbMigrate" ]]; then
    ./service/bin/jophiel db migrate
else 
    ./service/bin/init.sh $COMMAND
fi
