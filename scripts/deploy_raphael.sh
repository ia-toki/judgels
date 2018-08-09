#!/bin/bash

set -ex

cd "$(dirname "$0")"/../judgels-frontends/raphael

GENERATE_SOURCEMAP=false yarn build
rm -rf dist/build && mv build dist/

cd ../../deployment/ansible

ansible --version
ansible-playbook -e @../../deployment-repo/conf/global.yml -e judgels_version=$JUDGELS_VERSION playbooks/build-raphael.yml
ansible-playbook -e @../../deployment-repo/conf/global.yml -e judgels_version=$JUDGELS_VERSION playbooks/deploy-raphael.yml
