# Domain language

`GLOSSARY.md` defines the domain terms. Read it before naming anything in the code.

# Backend

Before placing or naming backend code (a package, an API path, a DTO or a Feign client), or running backend tests, read `judgels-backends/AGENTS.md`.

# Client

Before placing or naming a client API module or a query, read `judgels-client/AGENTS.md`.

# API naming

An endpoint has one name on every surface: the resource method, the Feign client method and the client API function are spelled the same.

The name is a verb followed by the resource's noun, which is the resource name without its container: `ContestAnnouncementResource.getAnnouncements`, `ContestItemSubmissionResource.getLatestItemSubmissions`, `UserRoleResource.setRoles`. A resource with no container keeps its whole name: `ContestResource.getContestBySlug`.

# Checking changes in the local app

After changing the client or an endpoint it calls, check the change in the locally running app: the client on `localhost:3000` and the server on `localhost:9101`. The developer starts the app; when it is down, ask them to start it.

In the locally running app, create and modify only what has a slug, username or name starting with `agent-`, e.g. a problem with slug `agent-batch-problem`. Call the existing API endpoints for this. Before creating a new `agent-` entry, reuse one from an earlier run.

Everything else is the developer's own data: read it, and leave it as it is.

`judgels-client/scripts/browser.mjs` opens a browser logged in as `superadmin`, which is also the user to call the API as. To check the app as any other user, e.g. a contestant, pass it an `agent-` user; give such a user its username as its password.

For graded submissions, submit a source file through the API: with `fake: true` under `judgels.grading` in `judgels-server.yml`, the server grades them itself. The name of the file picks the result, as `<verdict>[-<score>]`: `wa-40-solution.cpp` is Wrong Answer with a score of 40, and any other name is accepted.

# Commits and pull requests

Subject: `<area>: <Summary>`, with area lowercase and summary in sentence case, e.g. `contest: Allow higher division to participate unofficially`.

Area is the part of Judgels the change touches, e.g. `client`, `contest`, `ci`, `doc`, `test`. When several fit, pick the one that tells a reader scanning `git log` the most; when none fits, check `git log` for precedent before naming a new one.

Append ` (BREAKING)` to the area when an existing deployment breaks on upgrade unless its operator acts, e.g. a changed config key or format, or deployment templates that must be redeployed together with the app: `client (BREAKING): Move API version out of apiUrl`.

Body: two plain paragraphs of one or two sentences each. The first states the problem: what was wrong or missing. The second states the fix: how this change solves it.

The PR title is the commit subject. The PR description keeps the problem paragraph and expands the fix paragraph with what a reviewer needs: approach, trade-offs, notable details. Prefer a list when the fix has several parts, so a reviewer can scan it; add headings only when its length demands them.

Commit messages and PR descriptions end with their own content: omit tool attribution such as a "Generated with Claude Code" footer or a `Co-Authored-By` trailer.
