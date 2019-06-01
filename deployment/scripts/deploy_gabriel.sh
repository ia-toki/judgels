#!/bin/bash

set -ex

cd "$(dirname "$0")"/../../judgels-backends/gabriel/gabriel-dist

../../gradlew clean distTar
tar -xf build/distributions/gabriel-* --strip-components=1 -C build/distributions
rm build/distributions/*.tgz

cd ../../../deployment/ansible

ansible --version
ansible-playbook -e @dist/env.yml -e judgels_version=$JUDGELS_VERSION playbooks/build-gabriel.yml
