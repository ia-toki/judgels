#!/bin/bash

set -ex

cd deployment/ansible

ansible --version
ansible-playbook -e app_version=$APP_VERSION playbooks/release-judgels.yml
