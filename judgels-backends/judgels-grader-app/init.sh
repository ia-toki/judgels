#!/bin/bash

set -ex

exec ./bin/judgels-grader-app server var/conf/judgels-grader.yml
