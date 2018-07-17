#!/bin/bash

set -ex

cd "$(dirname "$0")"/../uriel-dist

../gradlew clean distTar
tar -xf build/distributions/uriel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz

ansible --version

cd ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/build-uriel.yml

cd ../../judgels/ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/deploy-uriel.yml
