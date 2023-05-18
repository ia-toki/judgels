#!/bin/bash

set -ex
exec ./bin/judgels-server-app $@ var/conf/judgels-server.yml
