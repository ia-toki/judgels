# Judgels

Judgels is a platform where authors write problems and lessons; users solve the problems in contests and in training, and read the lessons in training. It runs as three apps: the server (`judgels-backends/judgels-server-app`), which does everything except grading programming submissions; the grader (`judgels-backends/judgels-grader-app`), which does that grading; and the client (`judgels-client`), the web app that users, authors and admins use.

## Language

### Layers

**Catalog**:
The problems and lessons that exist independently of any contest or training, ready to be hosted in containers.
_Avoid_: Resource, Sandalphon

**Host**:
The part of Judgels that hosts catalog problems and lessons for users: contest or training. Each host puts them into its containers and owns whatever organizes them.

**Submission**:
A user's answer to a problem, or to one item of a bundle problem, made in a container. It is graded when it is made and again on each regrade; its latest grading gives its current verdict.

**Grading**:
Checking an answer against its problem to produce a verdict. Grading sees only the problem and the answer, never the submission, user or container behind it.

### Catalog

**Problem**:
A task users solve, with its statements, editorials and tags. It is either a programming problem or a bundle problem.

**Programming problem**:
A problem solved by submitting source code, graded against its test data.

**Bundle problem**:
A problem made of items (multiple-choice, short-answer or essay questions, plus statement-only items), each answered on its own.

**Lesson**:
A piece of reading material with its statements. It is in the catalog even though only training shows it today.

**Test submission**:
A submission an author makes against their own problem while writing it, outside any host.

### Hosts

**Contest**:
The host for time-boxed events in which contestants solve contest problems. Each contest is a single container.
_Avoid_: Uriel

**Training**:
The host for self-paced practice, the sibling of contest. It has no single root: its containers are chapters and problem sets, organized by a curriculum, courses and archives.
_Avoid_: Jerahmeel

**Curriculum**:
The single program that frames all courses, shown above the course list.

**Course**:
An ordered sequence of chapters.

**Chapter**:
A unit of chapter lessons and chapter problems, which courses include under an alias.

**Archive**:
A group of problem sets, such as the past editions of one competition.

**Problem set**:
A set of catalog problems hosted together in an archive, such as the problems of one past contest.
_Avoid_: Problemset (in prose)

**Container**:
A place where catalog problems and lessons are hosted under an alias, and where submissions are made: a contest, a chapter or a problem set. A test submission's container is the problem itself.

**Contest problem**, **Chapter problem**, **Problem set problem**, **Chapter lesson**:
A catalog problem or lesson hosted in a container under an alias, with the settings that container gives it.

### Grading

**Grading request**:
The problem, language and source files sent to the grader to grade a programming submission.

**Grader**:
The separate app that takes grading requests for programming problems and returns verdicts.

**Grading engine**:
How a programming problem is graded: batch, functional, interactive or output-only.

### Roles

**Contest admin**:
A user who can create and manage every contest.

**Problem admin**:
A user who can see, create and manage every problem.

**Training admin**:
A user who manages training, including every lesson.
