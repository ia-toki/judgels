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

ansible-playbook -c local -e @../../tlx-staging/jophiel_conf.yml playbooks/build-jophiel.yml
ansible-playbook -c local -e @../../tlx-staging/jophiel_conf.yml playbooks/build-jophiel-nginx.yml
ansible-playbook -e @../../tlx-staging/jophiel_conf.yml playbooks/deploy-jophiel.yml
