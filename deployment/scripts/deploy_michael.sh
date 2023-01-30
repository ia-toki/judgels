#!/bin/bash

set -ex

cd deployment/ansible

ansible --version
ansible-playbook -e @dist/env.yml playbooks/deploy-michael.yml
