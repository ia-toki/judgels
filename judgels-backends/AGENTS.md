# Code layout

Server packages, DTO packages (`judgels.api.…`), Feign clients and client API modules (`judgels-client/src/modules/api`) all follow the layers defined in `CONTEXT.md` at the repo root, so finding one tells you where the others are. API paths follow them under `/api/v4` only. Michael (`judgels.michael`) is exempt because it is being replaced.

| Layer | Packages | API paths |
|---|---|---|
| Catalog | `judgels.problem.*`, `judgels.lesson.*` | `/api/v4/{problems,lessons}/...`; render at `/api/v2/{problems,lessons}/{jid}/render/...` |
| Host | `judgels.contest.*`, `judgels.training.*` | `/api/v2/contests/...`; training at `/api/v2/{curriculums,courses,chapters,archives,problemsets}/...` and `/api/v4/training/...` |
| Submission | `judgels.submission.*` | none |
| Grading | `judgels.grading.*` | none |

Infrastructure with no domain meaning (`judgels.persistence`, `judgels.fs`, `judgels.mailer`, …) sits outside the layers.

Submission code is written once and reused by every host, and by the catalog for test submissions. Each of them gives it its own tables and passes the container as an opaque `containerJid`, which only it interprets.

Name a package after what its code does, not who uses it. Two questions place any class:
1. Would it still exist if training were deleted? If not, it goes under `judgels.training`.
2. Is it about a problem, lesson or submission itself, or about hosting one? The thing itself goes under `judgels.problem`, `judgels.lesson` or `judgels.submission`; hosting it is host code, which wraps it in host-specific classes, such as `ContestProblem`, `ChapterLesson` or `TrainingSubmissionResource`.

The role that manages a catalog item does not move its code: lessons stay in `judgels.lesson` although training admins manage them.

Prefix a training class or DTO with `Training` only when its name would otherwise clash with a catalog, submission or grading name, e.g. `TrainingProblemsResponse`, `TrainingSubmissionConfig`, `TrainingBundleItemSubmissionModel`. `Curriculum`, `Course`, `Chapter`, `Archive` and `ProblemSet` stay unprefixed because they are already unambiguous. Renaming a persistence model keeps its table name.

`/api/v4` holds paths designed by this layout; `/api/v2` holds the older paths, which stay as they are. A new endpoint joins its family's existing version (more course endpoints go under `/api/v2/courses`), and a new family goes under `/api/v4`.

Some packages still break this layout (e.g. `judgels.problemset`, `judgels.stats`, `judgels.problem.bundle.grading`); they will be moved to fit it. Place new code by this layout, not by its neighbours.
