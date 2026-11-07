# Domain language

`GLOSSARY.md` defines the domain terms. Read it before naming anything in the code.

# Backend

Before placing or naming backend code (a package, an API path, a DTO or a Feign client), or running backend tests, read `judgels-backends/AGENTS.md`.

# Client

Before placing or naming a client API module or a query, read `judgels-client/AGENTS.md`.

# API naming

An endpoint has one name on every surface: the resource method, the Feign client method and the client API function are spelled the same.

The name is a verb followed by the resource's noun, which is the resource name without its container: `ContestAnnouncementResource.getAnnouncements`, `ContestItemSubmissionResource.getLatestItemSubmissions`, `UserRoleResource.setRoles`. A resource with no container keeps its whole name: `ContestResource.getContestBySlug`.

# Verifying changes

Before finishing a task, verify it with the `verify` skill.
