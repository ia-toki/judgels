#!/bin/bash

set -ex

COMMAND=$1

if [[ -z ${COMMAND} ]]; then
	COMMAND="console"
fi

if [[ "$COMMAND" == "dbMigrate" ]]; then
    exec ./service/bin/jerahmeel db migrate var/conf/jerahmeel.yml
else 
    exec ./service/bin/init.sh $COMMAND
fi
