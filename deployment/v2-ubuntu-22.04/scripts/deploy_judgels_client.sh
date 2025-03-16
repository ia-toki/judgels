#!/bin/bash

set -ex

cd "$(dirname "$0")"/../ansible

ansible --version
ansible-playbook -e @env/vars.yml playbooks/deploy-judgels-client.yml
