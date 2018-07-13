#!/bin/bash

set -ex

cd "$(dirname "$0")"/../jophiel-dist

../gradlew clean distTar
tar -xf build/distributions/jophiel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz

ansible --version

cd ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/build-jophiel.yml

cd ../../judgels/ansible
ansible-playbook -e @../../deployment/conf/global.yml playbooks/deploy-jophiel.yml
