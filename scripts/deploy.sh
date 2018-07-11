#!/bin/bash

set -ex

cd "$(dirname "$0")"/../sealtiel-dist

../gradlew clean distTar
tar -xf build/distributions/sealtiel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz

ansible --version

cd ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/build-sealtiel.yml

cd ../../judgels/ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/deploy-sealtiel.yml
