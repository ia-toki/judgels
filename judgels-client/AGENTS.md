# API modules and queries

Client API modules go in `src/modules/api`, each named after its Feign client: `ContestProblemClient` matches `contestProblem.js`, which exports `contestProblemAPI`. Its functions are named as the root `AGENTS.md` describes.

Client queries go in `src/modules/queries`, each module holding the queries of the API module it is named after. A query is named after the API function it wraps:

- A query puts the container back in front and drops `get`: `contestAnnouncementAPI.getAnnouncements` is wrapped by `contestAnnouncementsQueryOptions`. A mutation keeps its verb first: `createContestAnnouncementMutationOptions`.
- The container is left out when the name already has it (`contestsPendingRatingQueryOptions`) or is about the actor (`myContestantStateQueryOptions`, `registerMyselfAsContestantMutationOptions`).
- When several queries wrap the same API function, each is prefixed with the scope it is shown in instead: `trainingSubmissionAPI.getSubmissions` is wrapped by `chapterSubmissionsQueryOptions`, `problemSetSubmissionsQueryOptions` and `profileSubmissionsQueryOptions`.
