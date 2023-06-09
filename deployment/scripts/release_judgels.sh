#!/bin/bash

set -ex

cd deployment/ansible

ansible --version
ansible-playbook -e judgels_version=$JUDGELS_VERSION playbooks/release-judgels.yml
