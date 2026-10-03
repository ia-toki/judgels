# Code layout

Server packages, DTO packages (`judgels.api.…`), Feign clients and client API modules (`judgels-client/src/modules/api`) all follow the layers defined in `GLOSSARY.md` at the repo root. Michael (`judgels.michael`) is exempt because it is being replaced.

| Layer | Packages | API paths |
|---|---|---|
| Catalog | `judgels.problem.*`, `judgels.lesson.*` | `/api/v4/{problems,lessons}/...`; render at `/api/v2/{problems,lessons}/{jid}/render/...` |
| Host | `judgels.contest.*`, `judgels.training.*` | `/api/v2/contests/...`; training at `/api/v2/{curriculums,courses,chapters,archives,problemsets}/...` and `/api/v4/training/...` |
| Submission | `judgels.submission.*` | none |
| Grading | `judgels.grading.*` | none |

Place new code by this layout, not by its neighbours: some packages still break it and will be moved to fit.

Name a package after what its code does, not who uses, manages or shows it. Two questions place any class:
1. Would it still exist if training were deleted? If not, it goes under `judgels.training`.
2. Is it about a problem, lesson or submission itself, or about hosting one? The thing itself goes under `judgels.problem`, `judgels.lesson` or `judgels.submission`; hosting it is host code, which wraps it in host-specific classes, such as `ContestProblem`, `ChapterLesson` or `TrainingSubmissionResource`.

Submission code is written once and reused by every host, and by the catalog for test submissions. Each of them gives it its own tables and passes the container as an opaque `containerJid`, which only it interprets.

Grading code sees only the problem and the answer, never the submission, user or container behind it.

Prefix a training class or DTO with `Training` only when its name would otherwise clash with a catalog, submission or grading name, e.g. `TrainingProblemsResponse`, `TrainingSubmissionConfig`, `TrainingBundleItemSubmissionModel`. `Curriculum`, `Course`, `Chapter`, `Archive` and `ProblemSet` stay unprefixed because they are already unambiguous.

The archangel names are deprecated: Jophiel (users), Sandalphon (catalog), Uriel (contest), Jerahmeel (training), Gabriel (grader) and Raphael (client). They survive mostly as table name prefixes, such as `uriel_contest` and `jophiel_user`. Name new code by the layers, and keep a table's name when renaming its persistence model.

API paths follow the layers under `/api/v4` only; `/api/v2` holds the older paths, which stay as they are. A new endpoint joins its family's existing version (more course endpoints go under `/api/v2/courses`), and a new family goes under `/api/v4`.

# Tests

Run `./gradlew :<module>:check` for the module you changed, from `judgels-backends`. Always name the module: a bare `./gradlew check` runs every module and takes too long.
