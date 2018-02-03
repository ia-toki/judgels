#!/bin/bash

cd "$(dirname "$0")"/../jophiel-dist/ansible

ansible --version

ansible-playbook -c local -e dockerhub_token=$dockerhub_token playbooks/build-jophiel.yml
ansible-playbook -c local -e dockerhub_token=$dockerhub_token playbooks/build-jophiel-nginx.yml
ansible-playbook -e db_username=$db_username -e db_password=$db_password playbooks/deploy-jophiel.yml
