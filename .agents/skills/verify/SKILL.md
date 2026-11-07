---
name: verify
description: Check a change in the locally running Judgels app, through its API and a browser. Use whenever a change needs checking beyond tests, or when the local app, its API or its data are involved.
---

# Verify a change in the local app

After changing the client or an endpoint it calls, check the change in the locally running app: the client on `localhost:3000` and the server on `localhost:9101`.

## 1. Make sure the app is running

The developer starts the app; when it is down, ask them to start it.

## 2. Use only `agent-` data

In the locally running app, create and modify only what has a slug, username or name starting with `agent-`, e.g. a problem with slug `agent-batch-problem`. Call the existing API endpoints for this. Before creating a new `agent-` entry, reuse one from an earlier run.

Everything else is the developer's own data: read it, and leave it as it is.

## 3. Check the change

`judgels-client/scripts/browser.mjs` opens a browser logged in as `superadmin`, which is also the user to call the API as. To check the app as any other user, e.g. a contestant, pass it an `agent-` user; give such a user its username as its password.

For graded submissions, submit a source file through the API: with `fake: true` under `judgels.grading` in `judgels-server.yml`, the server grades them itself. The name of the file picks the result, as `<verdict>[-<score>]`: `wa-40-solution.cpp` is Wrong Answer with a score of 40, and any other name is accepted.
