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

# Layout

Compose layout with `Flex` and `Box` from `@blueprintjs/labs`, not custom CSS: `Flex` for a row or a stack of children, `Box` for a container `div` that needs spacing.

- Space siblings with `gap` on their parent `Flex`, not a margin on each sibling.
- Spacing props are steps of 4px: `gap={2}` is 8px. The steps are 0, 0.5, 1, 2, ... 10.
- Pass `asChild` to `Flex` to lay out the children of an element that is not a `div`, e.g. a Blueprint `Card` or a `form`.
- Set a property on an element either with a prop or in CSS, never both: the prop wins.

To style a single element, do not wrap it in `<Box asChild>`. In order of preference:

1. Let its parent space it, with `gap`.
2. Use the component's own prop, e.g. `fill` on a `Button`.
3. Give it a class in a stylesheet, with spacing in steps of 4px.

These stay in a stylesheet as well:

- A layout that changes at a breakpoint: the props have no responsive form.
- A size in pixels: `width` and `height` props are 25, 50, 75, 100 (percent) or `auto`.
- Spacing that a `calc()` height elsewhere accounts for, e.g. the title above a full-height page: a step of 4px would no longer add up.
- An override of an element inside a Blueprint component, e.g. `.bp6-form-group`.
- Everything that is not layout: color, typography, border.

Delete a stylesheet, and its import, once it is empty.
