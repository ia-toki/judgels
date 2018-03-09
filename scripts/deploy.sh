#!/bin/bash

set -ex

cd "$(dirname "$0")"/../jophiel-dist

../gradlew clean distTar
tar -xf build/distributions/jophiel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz

cp dockerfiles/jophiel/Dockerfile build/
cp dockerfiles/jophiel/.dockerignore build/

cd ansible
ansible --version
cp ../../deployment/global/hosts .

mkdir -p roles/jophiel-deploy/templates
cp ../../deployment/jophiel/conf/jophiel.yml roles/jophiel-deploy/templates/jophiel.yml.j2

ansible-playbook -c local -e @../../deployment/global/env.yml playbooks/build-jophiel.yml
ansible-playbook -e @../../deployment/global/env.yml playbooks/deploy-jophiel.yml
