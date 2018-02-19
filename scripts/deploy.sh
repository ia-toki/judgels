#!/bin/bash

cd "$(dirname "$0")"/../jophiel-dist/ansible

ansible --version

ansible-playbook -c local -e @tlx-staging/jophiel_conf.yml playbooks/build-jophiel.yml
ansible-playbook -c local -e @tlx-staging/jophiel_conf.yml playbooks/build-jophiel-nginx.yml
ansible-playbook -e @tlx-staging/jophiel_conf.yml playbooks/deploy-jophiel.yml
