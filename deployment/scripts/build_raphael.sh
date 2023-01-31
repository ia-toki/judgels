#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-frontends/raphael

yarn
yarn build
rm -rf dist/build && mv build dist/

cd ../../deployment/ansible

ansible --version | cat -
ansible-playbook playbooks/build-raphael.yml
