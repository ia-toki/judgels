#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-client/raphael

yarn
yarn build

cd ..

rm -rf dist/build && mv raphael/build dist/

cd ../deployment/ansible

ansible --version 0</dev/null |& cat -
ansible-playbook playbooks/build-judgels-client.yml 0</dev/null |& cat -
