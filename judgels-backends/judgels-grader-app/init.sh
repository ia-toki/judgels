#!/bin/bash

set -ex
exec ./bin/judgels-grader-app $@ var/conf/judgels-grader.yml
