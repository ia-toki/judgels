#!/bin/bash

set -ex

cd "$(dirname "$0")"/../jophiel-dist

../gradlew clean distTar
tar -xf build/distributions/jophiel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz
mv build/distributions/var/conf/jophiel.yml.example build/distributions/var/conf/jophiel.yml

cp dockerfiles/jophiel/Dockerfile build/
cp dockerfiles/jophiel/.dockerignore build/

cd ansible
ansible --version
cp ../../tlx-staging/jophiel/ansible/* .

mkdir -p roles/jophiel-deploy/templates
cp ../../tlx-staging/jophiel/conf/jophiel.yml roles/jophiel-deploy/templates/jophiel.yml.j2
ansible-playbook -c local -e @../../tlx-staging/global.yml playbooks/build-jophiel.yml

ansible-playbook -c local -e @../../tlx-staging/global.yml playbooks/build-jophiel-nginx.yml

ansible-playbook -e @../../tlx-staging/global.yml playbooks/deploy-jophiel.yml
