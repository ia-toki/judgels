#!/bin/bash

set -ex

cd "$(dirname "$0")"/../judgels-backends/uriel/uriel-dist

../../gradlew clean distTar
tar -xf build/distributions/uriel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz
cp init.sh build/distributions

cd ../../../deployment/ansible

ansible --version
ansible-playbook -e @../../deployment-repo/conf/global.yml -e judgels_version=$JUDGELS_VERSION playbooks/build-uriel.yml
ansible-playbook -e @../../deployment-repo/conf/global.yml -e judgels_version=$JUDGELS_VERSION playbooks/deploy-uriel.yml
