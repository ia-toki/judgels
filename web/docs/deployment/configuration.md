---
sidebar_position: 4
---

# Configuration

This page explain how to further configure the Judgels deployment.

WIP

### Redeploying Judgels

After updating `vars.yml`, to actually apply the new configuration, run the following in `deployment/ansible`:

```
ansible-playbook -e @env/vars.yml playbooks/deploy.yml --tags=config
```
