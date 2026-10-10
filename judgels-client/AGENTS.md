# API modules and queries

Client API modules go in `src/modules/api`, each named after its Feign client: `ContestProblemClient` matches `contestProblem.js`, which exports `contestProblemAPI`. Its functions are named as the root `AGENTS.md` describes.

Client queries go in `src/modules/queries`, each module holding the queries of the API module it is named after. A query is named after the API function it wraps:

- A query puts the container back in front and drops `get`: `contestAnnouncementAPI.getAnnouncements` is wrapped by `contestAnnouncementsQueryOptions`. A mutation keeps its verb first: `createContestAnnouncementMutationOptions`.
- The container is left out when the name already has it (`contestsPendingRatingQueryOptions`) or is about the actor (`myContestantStateQueryOptions`, `registerMyselfAsContestantMutationOptions`).
- When several queries wrap the same API function, each is prefixed with the scope it is shown in instead: `trainingSubmissionAPI.getSubmissions` is wrapped by `chapterSubmissionsQueryOptions`, `problemSetSubmissionsQueryOptions` and `profileSubmissionsQueryOptions`.

# Query keys

A query key follows the same shape everywhere:

- It starts with its scope: `[container, containerJid]`, singular, for something inside a container (`['contest', contestJid, 'announcements']`), or the plural noun for a top-level list (`['contests']`). Singular and plural stay distinct so that invalidating a list does not invalidate everything inside each of its items.
- Then the path below the scope, as kebab-case literals: plural for a list, singular with its id for one item (`['chapter', chapterJid, 'problem', problemAlias, 'worksheet']`).
- The params object goes last, as `...(params ? [params] : [])`, so that the key without params is a prefix of every key with them.
- One endpoint has one key: two endpoints never share a key, and two queries never give the same endpoint and arguments different keys.
- An endpoint that spans containers is keyed under its own path, with the container among the params: every query wrapping `trainingSubmissionAPI.getSubmissions` uses `['training', 'submissions', 'programming', filter]`, where a chapter's list sets `containerJid` in the filter.
- A lookup by another field is `['<noun>-by-<field>', value]`: `['contest-by-slug', contestSlug]`.

Invalidate with the query options (`queryClient.invalidateQueries(contestProblemsQueryOptions(contestJid))`), not a hand-written key.

Bump `buster` in `src/index.jsx` whenever a key or the shape of its data changes; otherwise the old entries stay in `localStorage`.

Submission queries set `meta: { persist: false }`, which keeps them out of `localStorage`. Add it to every new submission query.
