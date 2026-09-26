---
sidebar_position: 4
---

# Configuration

This page explains how to further configure the Judgels deployment.

These configs are available in `vars.yml`.

### Java JVM options

Uncomment the following lines to set JVM options for the judgels server and grader apps:

```
# java_opts_judgels_server: -Xmx1g
# java_opts_judgels_grader: -Xmx1g
```

For example:

```
java_opts_judgels_server: -Xms512m -Xmx1g
```

### Grading worker threads per machine

By default, there will be one worker thread per grader VM:

- `grading_numWorkerThreads: 1`

If the number of CPUs in each of the grader VMs is lower/higher, we can set a different number appropriately, e.g.:

- `grading_numWorkerThreads: 2`

It means that each grader VM can have 2 concurrent grading executions.

### Redeploying Judgels

After updating `vars.yml`, to actually apply the new configuration, run the following in `deployment/v3-ubuntu-24.04/ansible`:

```
ansible-playbook -e @env/vars.yml playbooks/deploy.yml --tags=config
```
