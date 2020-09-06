#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/judgels-play/sandalphon

../../gradlew stage
rm -rf build/stage/main/conf
rm -rf build/stage/main/lib/org.webjars-*

cd ../../../deployment/ansible

ansible --version
ansible-playbook playbooks/build-sandalphon.yml
