#!/bin/bash

set -ex

COMMAND=$1

if [[ -z ${COMMAND} ]]; then
	COMMAND="console"
fi

if [[ "$COMMAND" == "console" ]]; then
    ./service/bin/init.sh console
elif [[ "$COMMAND" == "dbMigrate" ]]; then
    ./service/bin/uriel db migrate
fi
