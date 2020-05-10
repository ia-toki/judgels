#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/judgels-play/sandalphon

../../gradlew stage
rm -rf build/stage/main/conf
rm -rf build/stage/main/lib/org.webjars-*

cd ../../../deployment/ansible

ansible --version
ansible-playbook -e @dist/env.yml -e judgels_version=$JUDGELS_VERSION playbooks/build-sandalphon.yml
ansible-playbook -e @dist/env.yml -e judgels_version=$JUDGELS_VERSION playbooks/deploy-sandalphon.yml
