package judgels.jerahmeel.submission.programming;

import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.OK;
import static judgels.gabriel.api.Verdict.PENDING;
import static judgels.gabriel.api.Verdict.RUNTIME_ERROR;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;
import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.gabriel.api.GradingResultDetails;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.TestCaseResult;
import judgels.gabriel.api.TestGroupResult;
import judgels.gabriel.api.Verdict;
import judgels.jerahmeel.AbstractIntegrationTests;
import judgels.jerahmeel.JerahmeelIntegrationTestComponent;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.jerahmeel.api.course.CourseProgress;
import judgels.jerahmeel.api.course.chapter.CourseChapter;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemStats;
import judgels.jerahmeel.api.problem.ProblemTopStats;
import judgels.jerahmeel.api.problem.ProblemTopStatsEntry;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetProgress;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.user.UserStats;
import judgels.jerahmeel.api.user.UserTopStatsEntry;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jerahmeel.course.CourseStore;
import judgels.jerahmeel.course.chapter.CourseChapterStore;
import judgels.jerahmeel.persistence.ChapterModel;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.jerahmeel.persistence.ProblemSetModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.StatsUserChapterModel;
import judgels.jerahmeel.persistence.StatsUserCourseModel;
import judgels.jerahmeel.persistence.StatsUserModel;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemSetModel;
import judgels.jerahmeel.problemset.ProblemSetStore;
import judgels.jerahmeel.problemset.problem.ProblemSetProblemStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        CourseModel.class,
        CourseChapterModel.class,
        ChapterModel.class,
        ChapterProblemModel.class,
        ProblemSetModel.class,
        ProblemSetProblemModel.class,
        StatsUserModel.class,
        StatsUserChapterModel.class,
        StatsUserCourseModel.class,
        StatsUserProblemModel.class,
        StatsUserProblemSetModel.class})
class StatsProcessorIntegrationTests extends AbstractIntegrationTests {
    private static final String USER_JID_1 = "JIDUSER-1";
    private static final String USER_JID_2 = "JIDUSER-2";
    private static final String PROBLEM_JID_1 = "JIDPROG-1";
    private static final String PROBLEM_JID_2 = "JIDPROG-2";
    private static final String PROBLEM_JID_3 = "JIDPROG-3";

    private CourseStore courseStore;
    private CourseChapterStore courseChapterStore;
    private ChapterStore chapterStore;
    private ChapterProblemStore chapterProblemStore;
    private ProblemSetStore problemSetStore;
    private ProblemSetProblemStore problemSetProblemStore;
    private StatsProcessor statsProcessor;
    private StatsStore statsStore;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        JerahmeelIntegrationTestComponent component = createComponent(sessionFactory);

        courseStore = component.courseStore();
        courseChapterStore = component.courseChapterStore();
        chapterStore = component.chapterStore();
        chapterProblemStore = component.chapterProblemStore();
        problemSetStore = component.problemSetStore();
        problemSetProblemStore = component.problemSetProblemStore();
        statsProcessor = component.statsProcessor();
        statsStore = component.statsStore();
    }

    @Test
    void chapter_flow() {
        Course course = courseStore.createCourse(new CourseCreateData.Builder().slug("course").build());

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
                new ChapterProblem.Builder().alias("B").type(PROGRAMMING).problemJid(PROBLEM_JID_3).build()));

        assertCourseProgress(course.getJid(), 0, 3, 2);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 2);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 20, 100, 32000);

        assertCourseProgress(course.getJid(), 0, 3, 2);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 2);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 70, 100, 32000);

        assertCourseProgress(course.getJid(), 0, 3, 2);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 2);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 100, 32000);

        assertCourseProgress(course.getJid(), 1, 3, 2);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 0, 2);

        submit(USER_JID_1, chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 50, 100, 32000);

        assertCourseProgress(course.getJid(), 1, 3, 2);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 0, 2);

        submit(USER_JID_1, chapter2.getJid(), PROBLEM_JID_2, ACCEPTED, 100, 100, 32000);

        assertCourseProgress(course.getJid(), 1, 3, 2);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 1, 2);

        submit(USER_JID_1, chapter2.getJid(), PROBLEM_JID_3, OK, 100, 100, 32000);

        assertCourseProgress(course.getJid(), 2, 3, 2);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 2, 2);
    }

    @Test
    void problem_set_flow() {
        ProblemSet problemSet = problemSetStore.createProblemSet(new ProblemSetCreateData.Builder().slug("ps").build());

        problemSetProblemStore.setProblems(problemSet.getJid(), ImmutableList.of(
                new ProblemSetProblem.Builder().alias("A").type(PROGRAMMING).problemJid(PROBLEM_JID_1).build(),
                new ProblemSetProblem.Builder().alias("B").type(PROGRAMMING).problemJid(PROBLEM_JID_2).build()));

        assertProblemProgresses(PENDING, 0, PENDING, 0);
        assertProblemStats(0, 0, 0);
        assertProblemTopStats(ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
        assertProblemSetProgresses(problemSet.getJid(), 0, 0, 2);

        submit(USER_JID_1, problemSet.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 70, 100, 32000);

        assertProblemProgresses(WRONG_ANSWER, 70, PENDING, 0);
        assertProblemStats(70, 0, 1);
        assertProblemTopStats(
                ImmutableList.of(
                    new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(70).build()),
                ImmutableList.of(),
                ImmutableList.of());
        assertProblemSetProgresses(problemSet.getJid(), 70, 0, 2);

        submit(USER_JID_1, problemSet.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 50, 100, 32000);

        assertProblemProgresses(WRONG_ANSWER, 50, PENDING, 0);
        assertProblemStats(50, 0, 1);
        assertProblemTopStats(
                ImmutableList.of(
                        new ProblemTopStatsEntry.Builder().userJid(USER_JID_1).stats(50).build()),
                ImmutableList.of(),
                ImmutableList.of());
        assertProblemSetProgresses(problemSet.getJid(), 50, 0, 2);

        submit(USER_JID_2, problemSet.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 200, 40000);

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
        assertProblemSetProgresses(problemSet.getJid(), 50, 100, 2);

        submit(USER_JID_1, problemSet.getJid(), PROBLEM_JID_1, OK, 100, 50, 50000);

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
        assertProblemSetProgresses(problemSet.getJid(), 100, 100, 2);

        submit(USER_JID_1, problemSet.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 300, 30000);

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
        assertProblemSetProgresses(problemSet.getJid(), 100, 100, 2);

        submit(USER_JID_1, problemSet.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 30, 1000, 60000);

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
        assertProblemSetProgresses(problemSet.getJid(), 100, 100, 2);

        submit(USER_JID_1, problemSet.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 200, 30000);

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
        assertProblemSetProgresses(problemSet.getJid(), 100, 100, 2);

        submit(USER_JID_1, problemSet.getJid(), PROBLEM_JID_2, RUNTIME_ERROR, 40, 100, 32000);

        assertUserStats(USER_JID_1, 140, 2, ImmutableMap.of(
                ACCEPTED.getCode(), 1L,
                RUNTIME_ERROR.getCode(), 1L));

        assertUserStats(USER_JID_2, 100, 1, ImmutableMap.of(
                ACCEPTED.getCode(), 1L));

        assertUserTopStats(USER_JID_1, 140, USER_JID_2, 100);

        submit(USER_JID_2, problemSet.getJid(), PROBLEM_JID_2, RUNTIME_ERROR, 40, 100, 32000);

        assertUserTopStats(USER_JID_1, 140, USER_JID_2, 140);
    }

    private void submit(
            String userJid, String containerJid, String problemJid, Verdict verdict, int score, int time, int memory) {

        statsProcessor.accept(new Submission.Builder()
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

    private void assertCourseProgress(String courseJid, int solved, int total, int totalSolvable) {
        assertThat(statsStore.getCourseProgressesMap(USER_JID_1, ImmutableSet.of(courseJid)))
                .isEqualTo(ImmutableMap.of(
                        courseJid, new CourseProgress.Builder()
                                .solvedChapters(solved)
                                .totalChapters(total)
                                .totalSolvableChapters(totalSolvable)
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
        assertThat(statsStore.getTopUserStats(Optional.of(1), Optional.of(20)).getPage())
                .containsExactly(
                        new UserTopStatsEntry.Builder().userJid(userJid1).totalScores(total1).build(),
                        new UserTopStatsEntry.Builder().userJid(userJid2).totalScores(total2).build());
    }
}
