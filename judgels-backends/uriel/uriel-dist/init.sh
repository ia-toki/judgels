#!/bin/bash

set -ex

SERVICE=$1

if [[ ! -z ${SERVICE} ]]; then
    if [[ "$SERVICE" == "run" ]]; then
        ./service/bin/init.sh console
    elif [[ "$SERVICE" == "dbMigrate" ]]; then
        ./service/bin/uriel db migrate
    fi
fi