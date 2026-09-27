# Commits and pull requests

Subject: `<area>: <Summary>`, with area lowercase and summary in sentence case, e.g. `contest: Allow higher division to participate unofficially`.

Area is the part of Judgels the change touches, e.g. `client`, `contest`, `ci`, `doc`, `test`. When several fit, pick the one that tells a reader scanning `git log` the most; when none fits, check `git log` for precedent before naming a new one.

Append ` (BREAKING)` to the area when an existing deployment breaks on upgrade unless its operator acts, e.g. a changed config key or format, or deployment templates that must be redeployed together with the app: `client (BREAKING): Move API version out of apiUrl`.

Body: two plain paragraphs of one or two sentences each. The first states the problem: what was wrong or missing. The second states the fix: how this change solves it.

The PR title is the commit subject. The PR description keeps the problem paragraph and expands the fix paragraph with what a reviewer needs: approach, trade-offs, notable details. Prefer a list when the fix has several parts, so a reviewer can scan it; add headings only when its length demands them.

Commit messages and PR descriptions end with their own content: omit tool attribution such as a "Generated with Claude Code" footer or a `Co-Authored-By` trailer.
