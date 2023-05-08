package judgels.jerahmeel.api;

import judgels.BaseJudgelsServiceIntegrationTests;
import judgels.jerahmeel.api.archive.ArchiveService;
import judgels.jerahmeel.api.chapter.ChapterService;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonService;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemService;
import judgels.jerahmeel.api.course.CourseService;
import judgels.jerahmeel.api.course.chapter.CourseChapterService;
import judgels.jerahmeel.api.problemset.ProblemSetService;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemService;
import judgels.jophiel.api.user.User;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.problem.Problem;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestService;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseJerahmeelServiceIntegrationTests extends BaseJudgelsServiceIntegrationTests {
    protected static final String ADMIN = "admin";
    protected static final String USER = "user";
    protected static final String USER_A = "userA";
    protected static final String USER_B = "userB";

    protected static final String PROBLEM_1_SLUG = "problem1Slug";
    protected static final String PROBLEM_2_SLUG = "problem2Slug";
    protected static final String PROBLEM_3_SLUG = "problem3Slug";

    protected static final String LESSON_1_SLUG = "lesson1Slug";
    protected static final String LESSON_2_SLUG = "lesson2Slug";
    protected static final String LESSON_3_SLUG = "lesson3Slug";

    public static final String CONTEST_1_SLUG = "contest1Slug";
    public static final String CONTEST_2_SLUG = "contest2Slug";

    protected static User userA;
    protected static User userB;

    protected static AuthHeader userAHeader;
    protected static AuthHeader userBHeader;

    protected static Problem problem1;
    protected static Problem problem2;
    protected static Problem problem3;

    protected static Lesson lesson1;
    protected static Lesson lesson2;
    protected static Lesson lesson3;

    protected static Contest contest1;
    protected static Contest contest2;

    protected CourseService courseService = createService(CourseService.class);
    protected ChapterService chapterService = createService(ChapterService.class);
    protected CourseChapterService courseChapterService = createService(CourseChapterService.class);
    protected ChapterProblemService chapterProblemService = createService(ChapterProblemService.class);
    protected ChapterLessonService chapterLessonService = createService(ChapterLessonService.class);
    protected ArchiveService archiveService = createService(ArchiveService.class);
    protected ProblemSetService problemSetService = createService(ProblemSetService.class);
    protected ProblemSetProblemService problemSetProblemService = createService(ProblemSetProblemService.class);

    @BeforeAll
    static void setUpJerahmeel() {
        userA = createUser("userA");
        userAHeader = getHeader(userA);

        userB = createUser("userB");
        userBHeader = getHeader(userB);

        webTarget = createWebTarget();

        problem1 = createProblem(adminHeader, PROBLEM_1_SLUG);
        problem2 = createProblem(adminHeader, PROBLEM_2_SLUG);
        problem3 = createBundleProblem(adminHeader, PROBLEM_3_SLUG);

        lesson1 = createLesson(adminHeader, LESSON_1_SLUG);
        lesson2 = createLesson(adminHeader, LESSON_2_SLUG);
        lesson3 = createLesson(adminHeader, LESSON_3_SLUG);
    }

    protected static Contest createContest(String slug) {
        return createService(ContestService.class).createContest(adminHeader, new ContestCreateData.Builder()
                .slug(slug)
                .build());
    }
}
