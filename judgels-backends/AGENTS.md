# Code layout

Server packages, DTO packages (`judgels.api.…`), Feign clients and client API modules (`judgels-client/src/modules/api`) all follow the layers defined in `GLOSSARY.md` at the repo root. Michael (`judgels.michael`) is exempt because it is being replaced.

| Layer | Packages | API paths |
|---|---|---|
| Catalog | `judgels.catalog.problem.*`, `judgels.catalog.lesson.*`; `judgels.catalog.*` for what the two share | `/api/v4/{problems,lessons}/...`; render at `/api/v2/{problems,lessons}/{jid}/render/...` |
| Host | `judgels.contest.*`, `judgels.training.*` | `/api/v2/contests/...`; training at `/api/v2/{curriculums,courses,chapters,archives,problemsets}/...` and `/api/v4/training/...` |
| Submission | `judgels.submission.*` | none |
| Grading | `judgels.grading.*` | none |

Place new code by this layout, not by its neighbours: some packages still break it and will be moved to fit.

Code that belongs to no layer goes in `judgels.core.*`: the app chassis and the capabilities any layer may use, e.g. `core.fs`, `core.git`, `core.messaging`, `core.mailer`, `core.auth`. Core imports nothing else under `judgels`.

Submission code is written once and reused by every host, and by the catalog for test submissions. Each of them gives it its own tables and passes the container as an opaque `containerJid`, which only it interprets.

Grading code sees only the problem and the answer, never the submission, user or container behind it.

Prefix a training class or DTO with `Training` only when its name would otherwise clash with a catalog, submission or grading name, e.g. `TrainingProblemsResponse`, `TrainingSubmissionConfig`, `TrainingBundleItemSubmissionModel`. `Curriculum`, `Course`, `Chapter`, `Archive` and `ProblemSet` stay unprefixed because they are already unambiguous.

The archangel names are deprecated: Jophiel (users), Sandalphon (catalog), Uriel (contest), Jerahmeel (training), Gabriel (grader) and Raphael (client). They survive mostly as table name prefixes, such as `uriel_contest` and `jophiel_user`. Name new code by the layers, and keep a table's name when renaming its persistence model.

An API path names a resource, not its layer: `judgels.catalog.problem` serves `/api/v4/problems` and `judgels.training.course` serves `/api/v2/courses`. Prefix a path with `training/` only when the endpoint is training-wide, spanning containers instead of belonging to one chapter, problem set, course or archive, e.g. `/api/v4/training/problems`, `/api/v4/training/submissions/programming`, `/api/v4/training/stats/users`.

This naming holds under `/api/v4` only; `/api/v2` holds the older paths, which stay as they are. A new endpoint joins its family's existing version (more course endpoints go under `/api/v2/courses`), and a new family goes under `/api/v4`.

# Tests

Run `./gradlew :<module>:check -x integTest` for the module you changed, from `judgels-backends`. Always name the module: a bare `./gradlew check` runs every module and takes too long.

Leave the integration tests to CI: they take too long to run locally. The command above still compiles them, so a rename that breaks one fails here. Run `./gradlew :<module>:integTest --tests '<class>'` only for an integration test you changed.
