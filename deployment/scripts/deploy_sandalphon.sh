#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-legacy/sandalphon

../gradlew stagePlayBinaryDist
rm -rf build/stage/playBinary/conf
rm -rf build/stage/playBinary/lib/org.webjars-*

cd ../../deployment/ansible

ansible --version
ansible-playbook -e @../../deployment-repo/conf/global.yml -e judgels_version=$JUDGELS_VERSION playbooks/build-sandalphon.yml
ansible-playbook -e @../../deployment-repo/conf/global.yml -e judgels_version=$JUDGELS_VERSION playbooks/deploy-sandalphon.yml
