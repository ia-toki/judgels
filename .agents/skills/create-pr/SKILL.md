---
name: create-pr
description: Commit the current change and open a GitHub pull request for it, with the commit message and PR description in this repository's format. Use whenever committing, pushing a branch or creating a pull request in this repository.
---

# Create a pull request

A pull request holds one commit. Its message and the PR follow the format below; write both before running any git command.

## 1. Write the subject

`<area>: <Summary>`, with area lowercase and summary in sentence case, e.g. `contest: Allow higher division to participate unofficially`.

- **Area** is the part of Judgels the change touches, e.g. `client`, `contest`, `ci`, `doc`, `test`. When several fit, pick the one that tells a reader scanning `git log` the most. When none fits, run `git log --format=%s | sed 's/:.*//' | sort | uniq -c | sort -rn` for precedent before naming a new one.
- **Summary** starts with a verb in the imperative: `Show`, `Fix`, `Require`, `Store`.
- **Breaking:** append ` (BREAKING)` to the area when an existing deployment breaks on upgrade unless its operator acts, e.g. a changed config key or format, or deployment templates that must be redeployed together with the app: `client (BREAKING): Move API version out of apiUrl`. Check the diff for changes to config classes, `judgels-server.yml`, deployment files and migrations that need manual steps.

## 2. Write the commit body

Two plain paragraphs of one or two sentences each, with no labels, lists or headings. The first states the problem: what was wrong or missing, in the past tense. The second states the fix: how this change solves it, in the present tense.

Example:

```
client: Show archive first in problem set rows and stop muting usernames

Problem set rows showed the problem set description above the archive description, the reverse of the intended order. Usernames in these rows were also faded, because the row text was muted with an opacity that its children cannot override.

Rows now show the archive description first. Row text is muted with the secondary text colors instead, so usernames keep their full rating color.
```

## 3. Write the PR description

The PR title is the commit subject. The description reuses the commit body's two paragraphs in reverse order, each under its own heading: `## What` holds the fix paragraph rewritten to start with a verb in the imperative, like the subject's summary, then `## Why` holds the problem paragraph as is.

Then add a `## Details` heading listing what What and Why leave out: which classes, endpoints or migrations change and how, trade-offs and alternatives rejected, and work left for a follow-up. Put the list in a collapsed `<details>` block whose `<summary>` is `Click to view details`, with a blank line after `<summary>` and before `</details>` so that GitHub renders the Markdown inside. Prefer a list when there are several parts, so a reviewer can scan it. Leave out Details only when What already names everything the diff does.

```
## What

Accept users whose rating falls in a higher division at registration too, when the option is enabled.

## Why

The `allowHigherDivisionsUnofficially` division option was applied to ratings and scoreboards but not to registration. Higher-division users were stuck at `REGISTRABLE_WRONG_DIVISION`, so they could never join as unofficial contestants.

## Details

<details>
<summary>Click to view details</summary>

- `ContestContestantRoleChecker` accepts a user whose division number is lower than the contest's, and only when `allowHigherDivisionsUnofficially` is set.
- Ratings and scoreboards already treat these users as unofficial, so nothing else changes.

</details>
```

## 4. Commit, push and open the PR

Unless told specifically otherwise, every pull request targets `master`, from its own branch made from the latest `master` and pushed to `origin`.

Omit tool attribution such as a "Generated with Claude Code" footer or a `Co-Authored-By` trailer.
