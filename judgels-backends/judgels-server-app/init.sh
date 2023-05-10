#!/bin/bash

set -ex

if [[ "$1" == "dbMigrate" ]]; then
    exec ./bin/judgels-server-app db migrate var/conf/judgels-server.yml
else
    exec ./bin/judgels-server-app server var/conf/judgels-server.yml
fi
