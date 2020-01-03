package judgels.jerahmeel.submission.programming;

import static judgels.gabriel.api.Verdict.ACCEPTED;
import static judgels.gabriel.api.Verdict.WRONG_ANSWER;
import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
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
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
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
    private static final String USER_JID = "JIDUSER";
    private static final String PROBLEM_JID_1 = "JIDPROG-1";
    private static final String PROBLEM_JID_2 = "JIDPROG-2";
    private static final String PROBLEM_JID_3 = "JIDPROG-3";
    private static final String PROBLEM_JID_4 = "JIDPROG-4";

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

        courseChapterStore.setChapters(course.getJid(), ImmutableList.of(
                new CourseChapter.Builder().alias("01").chapterJid(chapter1.getJid()).build(),
                new CourseChapter.Builder().alias("02").chapterJid(chapter2.getJid()).build()));

        chapterProblemStore.setProblems(chapter1.getJid(), ImmutableList.of(
                new ChapterProblem.Builder().alias("A").type(PROGRAMMING).problemJid(PROBLEM_JID_1).build()));
        chapterProblemStore.setProblems(chapter2.getJid(), ImmutableList.of(
                new ChapterProblem.Builder().alias("A").type(PROGRAMMING).problemJid(PROBLEM_JID_2).build(),
                new ChapterProblem.Builder().alias("B").type(PROGRAMMING).problemJid(PROBLEM_JID_3).build()));

        ProblemSet problemSet =
                problemSetStore.createProblemSet(new ProblemSetCreateData.Builder().slug("pset1").build());

        problemSetProblemStore.setProblems(problemSet.getJid(), ImmutableList.of(
                new ProblemSetProblem.Builder().alias("A").type(PROGRAMMING).problemJid(PROBLEM_JID_4).build()));

        assertCourseProgress(course.getJid(), 0, 2);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 2);

        submit(chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 20, 100, 32000);

        assertCourseProgress(course.getJid(), 0, 2);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 2);

        submit(chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 70, 100, 32000);

        assertCourseProgress(course.getJid(), 0, 2);
        assertChapterProgresses(chapter1.getJid(), 0, 1, chapter2.getJid(), 0, 2);

        submit(chapter1.getJid(), PROBLEM_JID_1, ACCEPTED, 100, 100, 32000);

        assertCourseProgress(course.getJid(), 1, 2);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 0, 2);

        submit(chapter1.getJid(), PROBLEM_JID_1, WRONG_ANSWER, 50, 100, 32000);

        assertCourseProgress(course.getJid(), 1, 2);
        assertChapterProgresses(chapter1.getJid(), 1, 1, chapter2.getJid(), 0, 2);
    }

    private void submit(String containerJid, String problemJid, Verdict verdict, int score, int time, int memory) {
        statsProcessor.accept(new Submission.Builder()
                .id(1)
                .jid("JIDSUBM")
                .userJid(USER_JID)
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

    private void assertCourseProgress(String courseJid, int solved, int total) {
        assertThat(statsStore.getCourseProgressesMap(USER_JID, ImmutableSet.of(courseJid)))
                .isEqualTo(ImmutableMap.of(
                        courseJid, new CourseProgress.Builder()
                                .solvedChapters(solved)
                                .totalChapters(total)
                                .build()));
    }

    private void assertChapterProgresses(
            String chapterJid1, int solved1, int total1,
            String chapterJid2, int solved2, int total2) {

        assertThat(statsStore.getChapterProgressesMap(USER_JID, ImmutableSet.of(chapterJid1, chapterJid2)))
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
}
