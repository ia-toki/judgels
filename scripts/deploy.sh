#!/bin/bash

set -ex

cd "$(dirname "$0")"/..

GENERATE_SOURCEMAP=false yarn build
mkdir -p dist/build
rm -rf dist/build/build && mv build dist/build/

cp dist/dockerfiles/raphael/* dist/build/
cp dist/dockerfiles/raphael/.dockerignore dist/build/

cd dist/ansible
ansible --version
cp ../../deployment/global/hosts .

mkdir -p roles/raphael-deploy/templates
cp ../../deployment/raphael/conf/raphael.js roles/raphael-deploy/templates/raphael.js.j2

ansible-playbook -c local -e @../../deployment/global/env.yml playbooks/build-raphael.yml
ansible-playbook -e @../../deployment/global/env.yml playbooks/deploy-raphael.yml
