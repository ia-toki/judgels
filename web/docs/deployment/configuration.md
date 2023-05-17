---
sidebar_position: 4
---

# Configuration

This page explain how to further configure the Judgels deployment.

These configs are available in `vars.yml`.

### Java max heap sizes

These are the default Java `-Xmx` max heap size for Judgels server and grader:

- `judgels_server_xmx`: `1g`
- `judgels_grader_xmx`: `1g`

We feel that the above sizes are sufficient, but they are configurable just in case.

### User session limits

- `jophiel_session_maxConcurrentSessionsPerUser`
   - Max number of allowed concurrent sessions (logins) per user. Set to `-1` for unlimited number.
- `jophiel_session_disableLogout`
   - Whether to disable logout.

For example, in an on-site contest setting, we can set the following combination to disallow remote login and to prevent logout:

- `jophiel_session_maxConcurrentSessionsPerUser`: `1`
- `jophiel_session_disableLogout`: `true`

### Grading worker threads per machine

By default, there will be 2 worker threads per grader VM:

- `gabriel_grading_numWorkerThreads: 2`

It means that each grader VM can have 2 concurrent grading executions.

If the number of CPUs in each of the grader VMs is lower/higher, we can set a different number appropriately.

### Redeploying Judgels

After updating `vars.yml`, to actually apply the new configuration, run the following in `deployment/ansible`:

```
ansible-playbook -e @env/vars.yml playbooks/deploy.yml --tags=config
```
