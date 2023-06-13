---
sidebar_position: 4
---

# Configuration

This page explain how to further configure the Judgels deployment.

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

### User session limits

- `jophiel_session_maxConcurrentSessionsPerUser`
   - Max number of allowed concurrent sessions (logins) per user. Set to `-1` for unlimited number.
- `jophiel_session_disableLogout`
   - Whether to disable logout.

For example, in an on-site contest setting, we can set the following combination to disallow remote login and to prevent logout:

- `jophiel_session_maxConcurrentSessionsPerUser`: `1`
- `jophiel_session_disableLogout`: `true`

### Grading worker threads per machine

By default, there will be one worker threads per grader VM:

- `gabriel_grading_numWorkerThreads: 1`

If the number of CPUs in each of the grader VMs is lower/higher, we can set a different number appropriately, e.g.:

- `gabriel_grading_numWorkerThreads: 2`

It means that each grader VM can have 2 concurrent grading executions.

### Redeploying Judgels

After updating `vars.yml`, to actually apply the new configuration, run the following in `deployment/ansible`:

```
ansible-playbook -e @env/vars.yml playbooks/deploy.yml --tags=config
```
