#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/judgels-server-app

../gradlew clean distTar
tar -xf build/distributions/judgels-server-app-* --strip-components=1 -C build/distributions
rm build/distributions/*.tar
cp init.sh build/distributions

cd ../../deployment/ansible

ansible --version
ansible-playbook playbooks/build-judgels-server.yml
