#!/bin/bash

set -ex

cd deployment/ansible

ansible --version
ansible-playbook -e app_version=$app_version playbooks/release-judgels.yml
