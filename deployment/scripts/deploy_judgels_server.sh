#!/bin/bash

set -ex

cd deployment/ansible

ansible --version
ansible-playbook -e @env/vars.yml playbooks/deploy-judgels-server.yml
