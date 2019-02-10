#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-legacy/jerahmeel

../gradlew stagePlayBinaryDist
rm -rf build/stage/playBinary/conf
rm -rf build/stage/playBinary/lib/org.webjars-*

cd ../../deployment/ansible

ansible --version
ansible-playbook -e @dist/env.yml -e judgels_version=$JUDGELS_VERSION playbooks/build-jerahmeel.yml
