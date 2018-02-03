#!/bin/bash

cd jophiel-dist/ansible

ansible-playbook playbooks/build-jophiel.yml -e dockerhub_token=$dockerhub_token
ansible-playbook playbooks/build-jophiel-nginx.yml -e dockerhub_token=$dockerhub_token
ansible-playbook playbooks/deploy-jophiel.yml -e db_user=$db_user -e db_password=$db_password
