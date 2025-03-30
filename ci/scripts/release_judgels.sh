#!/bin/bash

set -ex

cd "$(dirname "$0")"/../ansible

ansible --version
ansible-playbook -e app_version=$APP_VERSION playbooks/release-judgels.yml
