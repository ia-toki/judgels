#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/michael/michael-dist

../../gradlew clean distTar
tar -xf build/distributions/michael-* --strip-components=1 -C build/distributions
rm build/distributions/*.tar
cp init.sh build/distributions

cd ../../../deployment/ansible

ansible --version
ansible-playbook playbooks/build-michael.yml
