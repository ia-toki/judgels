#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/judgels-grader-app

../gradlew clean distTar
tar -xf build/distributions/judgels-grader-* --strip-components=1 -C build/distributions
rm build/distributions/*.tar
cp init.sh build/distributions

cd -
cd "$(dirname "$0")"/../ansible

ansible --version
ansible-playbook playbooks/build-judgels-grader.yml
