#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-client

yarn
yarn build

rm -rf dist/build && mv build dist/

cd ../deployment/ansible

ansible --version 0</dev/null |& cat -
ansible-playbook  -e app_version=3.0.0-alpha playbooks/build-judgels-client.yml 0</dev/null |& cat -
