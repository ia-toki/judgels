#!/bin/bash

set -ex

cd "$(dirname "$0")"/..

GENERATE_SOURCEMAP=false yarn build
rm -rf dist/build && mv build dist/

ansible --version

cd dist/ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/build-raphael.yml

cd ../../judgels/ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/deploy-raphael.yml
