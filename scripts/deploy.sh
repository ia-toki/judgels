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
cp ../../tlx-staging/global/hosts .

mkdir -p roles/jophiel-deploy/templates
cp ../../tlx-staging/jophiel/conf/jophiel.yml roles/jophiel-deploy/templates/jophiel.yml.j2

ansible-playbook -c local -e @../../tlx-staging/global/env.yml playbooks/build-jophiel.yml
ansible-playbook -e @../../tlx-staging/global/env.yml playbooks/deploy-jophiel.yml
