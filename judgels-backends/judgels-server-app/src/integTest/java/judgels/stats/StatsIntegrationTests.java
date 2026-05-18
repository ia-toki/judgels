package judgels.stats;

import static judgels.api.problem.ProblemType.BUNDLE;
import static judgels.api.problem.ProblemType.PROGRAMMING;
import static judgels.grading.api.Verdict.ACCEPTED;
import static judgels.grading.api.Verdict.OK;
import static judgels.grading.api.Verdict.PENDING;
import static judgels.grading.api.Verdict.RUNTIME_ERROR;
import static judgels.grading.api.Verdict.WRONG_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.archive.Archive;
import judgels.api.archive.ArchiveCreateData;
import judgels.api.chapter.Chapter;
import judgels.api.chapter.ChapterCreateData;
import judgels.api.chapter.ChapterProgress;
import judgels.api.chapter.problem.ChapterProblem;
import judgels.api.course.Course;
import judgels.api.course.CourseCreateData;
import judgels.api.course.CourseProgress;
import judgels.api.course.chapter.CourseChapter;
import judgels.api.problem.ProblemProgress;
import judgels.api.problem.ProblemStats;
import judgels.api.problem.ProblemTopStats;
import judgels.api.problem.ProblemTopStatsEntry;
import judgels.api.problemset.ProblemSet;
import judgels.api.problemset.ProblemSetCreateData;
import judgels.api.problemset.ProblemSetProgress;
import judgels.api.problemset.problem.ProblemSetProblem;
import judgels.api.stats.UserStats;
import judgels.api.stats.UserTopStatsEntry;
import judgels.api.submission.bundle.ItemSubmission;
import judgels.api.submission.programming.Grading;
import judgels.api.submission.programming.Submission;
import judgels.archive.ArchiveStore;
import judgels.chapter.ChapterStore;
import judgels.chapter.problem.ChapterProblemStore;
import judgels.course.CourseStore;
import judgels.course.chapter.CourseChapterStore;
import judgels.grading.api.GradingResultDetails;
import judgels.grading.api.SandboxExecutionResult;
import judgels.grading.api.SandboxExecutionStatus;
import judgels.grading.api.TestCaseResult;
import judgels.grading.api.TestGroupResult;
import judgels.grading.api.Verdict;
import judgels.persistence.ArchiveModel;
import judgels.persistence.ChapterModel;
import judgels.persistence.ChapterProblemModel;
import judgels.persistence.CourseChapterModel;
import judgels.persistence.CourseModel;
import judgels.persistence.ProblemContestModel;
import judgels.persistence.ProblemSetModel;
import judgels.persistence.ProblemSetProblemModel;
import judgels.persistence.StatsUserModel;
import judgels.persistence.StatsUserProblemModel;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.problemset.ProblemSetStore;
import judgels.problemset.problem.ProblemSetProblemStore;
import judgels.training.BaseTrainingIntegrationTests;
import judgels.training.TrainingIntegrationTestComponent;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        ArchiveModel.class,
        CourseModel.class,
        CourseChapterModel.class,
        ChapterModel.class,
        ChapterProblemModel.class,
        ProblemSetModel.class,
        ProblemSetProblemModel.class,
        ProblemContestModel.class,
        StatsUserModel.class,
        StatsUserProblemModel.class})
class StatsIntegrationTests extends BaseTrainingIntegrationTests {
    private static final String USER_JID_1 = "JIDUSER-1";
    private static final String USER_JID_2 = "JIDUSER-2";
    private static final String PROBLEM_JID_1 = "JIDPROG-1";
    private static final String PROBLEM_JID_2 = "JIDPROG-2";
    private static final String PROBLEM_JID_3 = "JIDPROG-3";
    private static final String PROBLEM_JID_4 = "JIDBUND-4";

    private CourseStore courseStore;
    private CourseChapterStore courseChapterStore;
    private ChapterStore chapterStore;
    private ChapterProblemStore chapterProblemStore;
    private ArchiveStore archiveStore;
    private ProblemSetStore problemSetStore;
    private ProblemSetProblemStore problemSetProblemStore;
    private StatsStore statsStore;

    private judgels.training.submission.programming.StatsProcessor programmingStatsProcessor;
    private judgels.training.submission.bundle.StatsProcessor bundleStatsProcessor;


    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        TrainingIntegrationTestComponent component = createComponent(sessionFactory);

        courseStore = component.courseStore();
        courseChapterStore = component.courseChapterStore();
        chapterStore = component.chapterStore();
        chapterProblemStore = component.chapterProblemStore();
        archiveStore = component.archiveStore();
        problemSetStore = component.problemSetStore();
        problemSetProblemStore = component.problemSetProblemStore();
        statsStore = component.statsStore();
        programmingStatsProcessor = component.programmingStatsProcessor();
        bundleStatsProcessor = component.bundleStatsProcessor();
    }

    @Test
    void chapter_flow() {
        Course course = courseStore.createCourse(new CourseCreateData.Builder().slug("course").name("Course").build());

        Chapter chapter1 = chapterStore.createChapter(new ChapterCreateData.Builder().name("chapter1").build());
        Chapter chapter2 = chapterStore.createChapter(new ChapterCreateData.Builder().name("chapter2").build());
        Chapter chapter3 = chapterStore.createChapter(new ChapterCreateData.Builder().name("chapter3").build());

        courseChapterStore.setChapters(course.getJid(), ImmutableList.of(
                new CourseChapter.Builder().alias("01").chapterJid(chapter1.getJid()).build(),
                new CourseChapter.Builder().alias("02").chapterJid(chapter2.getJid()).build(),
                new CourseChapter.Builder().alias("03").chapterJid(chapter3.getJid()).build()));

        chapterProblemStore.setProblems(chapter1.getJid(), ImmutableList.of(
                new ChapterProblem.Builder().alias("A").type(PROGRAMMING).problemJid(PROBLEM_JID_1).build()));
        chapterProblemStore.setProblems(chapter2.getJid(), ImmutableList.of(
                new ChapterProblem.Builder().alias("A").type(PROGRAMMING).problemJid(PROBLEM_JID_2).build(),
                new ChapterProblem.Builder().alias("B").type(PROGRAMMING).problemJid(PROBLEM_JID_3).build(),
                new ChapterProblem.Builder().alias("C").type(BUNDLE).problemJid(PROBLEM_JID_4).build()));

        submit(USER_JID_1, "randomJid", "randomJid", ACCEPTED, 100, 100, 32000);

        assertCourseProgress(course.getJid(), 0, 4);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 3);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 20, 100, 32000);

        assertCourseProgress(course.getJid(), 0, 4);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 3);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 70, 100, 32000);

        assertCourseProgress(course.getJid(), 0, 4);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 3);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 0, 32000);

        assertCourseProgress(course.getJid(), 1, 4);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 0, 3);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 50, 100, 32000);

        assertCourseProgress(course.getJid(), 1, 4);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 0, 3);

        submit(USER_JID_1, chapter2.getJid(), PROBLEM_JID_2, ACCEPTED, 100, 100, 32000);

        assertCourseProgress(course.getJid(), 2, 4);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 1, 3);

        submit(USER_JID_1, chapter2.getJid(), PROBLEM_JID_3, OK, 100, 100, 32000);

        assertCourseProgress(course.getJid(), 3, 4);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 2, 3);

        submitItem(USER_JID_1, chapter2.getJid(), PROBLEM_JID_4, judgels.api.submission.bundle.Verdict.WRONG_ANSWER);

        assertCourseProgress(course.getJid(), 3, 4);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 2, 3);

        submitItem(USER_JID_1, chapter2.getJid(), PROBLEM_JID_4, judgels.api.submission.bundle.Verdict.ACCEPTED);

        assertCourseProgress(course.getJid(), 4, 4);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 3, 3);
    }

    @Test
    void problem_set_flow() {
        Archive archive = archiveStore.createArchive(new ArchiveCreateData.Builder()
                .slug("archive")
                .name("Archive")
                .category("Category")
                .build());
        ProblemSet problemSet1 = problemSetStore.createProblemSet(new ProblemSetCreateData.Builder()
                .slug("ps1")
                .name("Problem Set 1")
                .archiveSlug(archive.getSlug())
                .build());
        ProblemSet problemSet2 = problemSetStore.createProblemSet(new ProblemSetCreateData.Builder()
                .slug("ps2")
                .name("Problem Set 2")
                .archiveSlug(archive.getSlug())
                .build());

        problemSetProblemStore.setProblems(problemSet1.getJid(), ImmutableList.of(
                new ProblemSetProblem.Builder().alias("A").type(PROGRAMMING).problemJid(PROBLEM_JID_1).build(),
                new ProblemSetProblem.Builder().alias("B").type(PROGRAMMING).problemJid(PROBLEM_JID_2).build()));

        problemSetProblemStore.setProblems(problemSet2.getJid(), ImmutableList.of(
                new ProblemSetProblem.Builder().alias("P-1").type(PROGRAMMING).problemJid(PROBLEM_JID_1).build()));

        submit(USER_JID_1, "randomJid", "randomJid", ACCEPTED, 100, 100, 32000);

        assertProblemProgresses(PENDING, 0, PENDING, 0);
        assertProblemStats(0, 0, 0);
        assertProblemTopStats(ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
        assertProblemSetProgresses(problemSet1.getJid(), 0, 0, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 0, 0, 1);

        // It doesn't matter which problemset we submit for PROBLEM_JID_1 -- both problemset stats will be updated
        submit(USER_JID_1, problemSet1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 70, 100, 32000);

        assertProblemProgresses(WRONG_ANSWER, 70, PENDING, 0);
        assertProblemStats(70, 0, 1);
        assertProblemTopStats(
                ImmutableList.of(
                    new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(70).build()),
                ImmutableList.of(),
                ImmutableList.of());
        assertProblemSetProgresses(problemSet1.getJid(), 70, 0, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 70, 0, 1);

        // It doesn't matter which problemset we submit for PROBLEM_JID_1 -- both problemset stats will be updated
        submit(USER_JID_1, problemSet2.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 50, 100, 32000);

        assertProblemProgresses(WRONG_ANSWER, 50, PENDING, 0);
        assertProblemStats(50, 0, 1);
        assertProblemTopStats(
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(50).build()),
                ImmutableList.of(),
                ImmutableList.of());
        assertProblemSetProgresses(problemSet1.getJid(), 50, 0, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 50, 0, 1);

        submit(USER_JID_2, problemSet1.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 200, 40000);

        assertProblemProgresses(WRONG_ANSWER, 50, ACCEPTED, 100);
        assertProblemStats(150, 1, 2);
        assertProblemTopStats(
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(100).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(50).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(200).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(40000).build()));
        assertProblemSetProgresses(problemSet1.getJid(), 50, 100, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 50, 100, 1);

        submit(USER_JID_1, problemSet1.getJid(), PROBLEM_JID_1, OK, 100, 50, 50000);

        assertProblemProgresses(ACCEPTED, 100, ACCEPTED, 100);
        assertProblemStats(200, 2, 2);
        assertProblemTopStats(
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(100).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(100).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(50).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(200).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(40000).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(50000).build()));
        assertProblemSetProgresses(problemSet1.getJid(), 100, 100, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 100, 100, 1);

        submit(USER_JID_1, problemSet1.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 300, 30000);

        assertProblemProgresses(ACCEPTED, 100, ACCEPTED, 100);
        assertProblemStats(200, 2, 2);
        assertProblemTopStats(
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(100).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(100).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(200).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(300).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(30000).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(40000).build()));
        assertProblemSetProgresses(problemSet1.getJid(), 100, 100, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 100, 100, 1);

        submit(USER_JID_1, problemSet1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 30, 1000, 60000);

        assertProblemProgresses(ACCEPTED, 100, ACCEPTED, 100);
        assertProblemStats(200, 2, 2);
        assertProblemTopStats(
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(100).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(100).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(200).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(300).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(30000).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(40000).build()));
        assertProblemSetProgresses(problemSet1.getJid(), 100, 100, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 100, 100, 1);

        submit(USER_JID_1, problemSet1.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 200, 30000);

        assertProblemProgresses(ACCEPTED, 100, ACCEPTED, 100);
        assertProblemStats(200, 2, 2);
        assertProblemTopStats(
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(100).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(100).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(200).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(200).build()),
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(30000).build(),
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_2).stats(40000).build()));
        assertProblemSetProgresses(problemSet1.getJid(), 100, 100, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 100, 100, 1);

        submit(USER_JID_1, problemSet1.getJid(), PROBLEM_JID_2, RUNTIME_ERROR, 40, 100, 32000);

        assertProblemSetProgresses(problemSet1.getJid(), 140, 100, 2);
        assertProblemSetProgresses(problemSet2.getJid(), 100, 100, 1);

        assertUserStats(USER_JID_1, 140, 2, ImmutableMap.of(
                ACCEPTED.getCode(), 1L,
                RUNTIME_ERROR.getCode(), 1L));

        assertUserStats(USER_JID_2, 100, 1, ImmutableMap.of(
                ACCEPTED.getCode(), 1L));

        assertUserTopStats(USER_JID_1, 140, USER_JID_2, 100);

        submit(USER_JID_2, problemSet1.getJid(), PROBLEM_JID_2, RUNTIME_ERROR, 40, 100, 32000);

        assertUserTopStats(USER_JID_1, 140, USER_JID_2, 140);
    }

    private void submit(
            String userJid, String containerJid, String problemJid, Verdict verdict, int score, int time, int memory) {

        programmingStatsProcessor.accept(new Submission.Builder()
                .id(1)
                .jid("JIDSUBM")
                .userJid(userJid)
                .containerJid(containerJid)
                .problemJid(problemJid)
                .gradingEngine("Batch")
                .gradingLanguage("Cpp")
                .time(Instant.now())
                .latestGrading(new Grading.Builder()
                        .id(1)
                        .jid("JIDGRAD")
                        .verdict(verdict)
                        .score(score)
                        .details(new GradingResultDetails.Builder()
                                .addTestDataResults(new TestGroupResult.Builder()
                                        .id(1)
                                        .addTestCaseResults(new TestCaseResult.Builder()
                                                .verdict(verdict)
                                                .score("" + score)
                                                .executionResult(new SandboxExecutionResult.Builder()
                                                        .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                                                        .time(time)
                                                        .memory(memory)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build());
    }

    private void submitItem(String userJid, String containerJid, String problemJid, judgels.api.submission.bundle.Verdict verdict) {
        judgels.api.submission.bundle.Grading grading = new judgels.api.submission.bundle.Grading.Builder()
                .verdict(verdict)
                .build();
        bundleStatsProcessor.accept(new ItemSubmission.Builder()
                .jid("JIDSUBB")
                .userJid(userJid)
                .containerJid(containerJid)
                .problemJid(problemJid)
                .itemJid("itemJid")
                .answer("a")
                .time(Instant.now())
                .grading(grading)
                .build(), Map.of("itemJid", Optional.of(grading)));
    }

    private void assertCourseProgress(String courseJid, int solvedProblems, int totalProblems) {
        assertThat(statsStore.getCourseProgressesMap(USER_JID_1, ImmutableSet.of(courseJid)))
                .isEqualTo(ImmutableMap.of(
                        courseJid, new CourseProgress.Builder()
                                .solvedProblems(solvedProblems)
                                .totalProblems(totalProblems)
                                .build()));
    }

    private void assertChapterProgresses(
            String chapterJid1, int solved1, int total1,
            String chapterJid2, int solved2, int total2) {

        assertThat(statsStore.getChapterProgressesMap(USER_JID_1, ImmutableSet.of(chapterJid1, chapterJid2)))
                .isEqualTo(ImmutableMap.of(
                        chapterJid1, new ChapterProgress.Builder()
                                .solvedProblems(solved1)
                                .totalProblems(total1)
                                .build(),
                        chapterJid2, new ChapterProgress.Builder()
                                .solvedProblems(solved2)
                                .totalProblems(total2)
                                .build()));
    }

    private void assertProblemProgresses(Verdict verdict1, int score1, Verdict verdict2, int score2) {
        assertThat(statsStore.getProblemProgressesMap(USER_JID_1, ImmutableSet.of(PROBLEM_JID_1)).get(PROBLEM_JID_1))
                .isEqualTo(new ProblemProgress.Builder()
                        .verdict(verdict1.getCode())
                        .score(score1)
                        .build());
        assertThat(statsStore.getProblemProgressesMap(USER_JID_2, ImmutableSet.of(PROBLEM_JID_1)).get(PROBLEM_JID_1))
                .isEqualTo(new ProblemProgress.Builder()
                        .verdict(verdict2.getCode())
                        .score(score2)
                        .build());
    }

    private void assertProblemStats(int total, int accepted, int tried) {
        assertThat(statsStore.getProblemStatsMap(ImmutableSet.of(PROBLEM_JID_1)).get(PROBLEM_JID_1))
                .isEqualTo(new ProblemStats.Builder()
                        .totalScores(total)
                        .totalUsersAccepted(accepted)
                        .totalUsersTried(tried)
                        .build());
    }

    private void assertProblemTopStats(
            List<ProblemTopStatsEntry> topScore,
            List<ProblemTopStatsEntry> topTime,
            List<ProblemTopStatsEntry> topMemory) {

        assertThat(statsStore.getProblemTopStats(PROBLEM_JID_1))
                .isEqualTo(new ProblemTopStats.Builder()
                        .topUsersByScore(topScore)
                        .topUsersByTime(topTime)
                        .topUsersByMemory(topMemory)
                        .build());
    }

    private void assertProblemSetProgresses(String problemSetJid, int score1, int score2, int total) {
        assertThat(statsStore.getProblemSetProgressesMap(USER_JID_1, ImmutableSet.of(problemSetJid)).get(problemSetJid))
                .isEqualTo(new ProblemSetProgress.Builder()
                        .score(score1)
                        .totalProblems(total)
                        .build());
        assertThat(statsStore.getProblemSetProgressesMap(USER_JID_2, ImmutableSet.of(problemSetJid)).get(problemSetJid))
                .isEqualTo(new ProblemSetProgress.Builder()
                        .score(score2)
                        .totalProblems(total)
                        .build());
    }

    private void assertUserStats(String userJid, int total, int tried, Map<String, Long> verdicts) {
        assertThat(statsStore.getUserStats(userJid))
                .isEqualTo(new UserStats.Builder()
                        .totalScores(total)
                        .totalProblemsTried(tried)
                        .totalProblemVerdictsMap(verdicts)
                        .build());
    }

    private void assertUserTopStats(String userJid1, int total1, String userJid2, int total2) {
        assertThat(statsStore.getTopUserStats(1, 20).getPage())
                .containsExactly(
                        new UserTopStatsEntry.Builder().userJid(userJid1).totalScores(total1).build(),
                        new UserTopStatsEntry.Builder().userJid(userJid2).totalScores(total2).build());
    }
}
