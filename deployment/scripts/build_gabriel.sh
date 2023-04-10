#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/gabriel/gabriel-dist

../../gradlew clean distTar
tar -xf build/distributions/gabriel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tar
cp init.sh build/distributions

cd ../../../deployment/ansible

ansible --version
ansible-playbook playbooks/build-gabriel.yml
