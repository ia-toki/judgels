#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/sealtiel/sealtiel-dist

../../gradlew clean distTar
tar -xf build/distributions/sealtiel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz

cd ../../../deployment/ansible

ansible --version
ansible-playbook playbooks/build-sealtiel.yml
