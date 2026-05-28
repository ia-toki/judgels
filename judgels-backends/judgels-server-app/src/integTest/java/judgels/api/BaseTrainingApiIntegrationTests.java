package judgels.api;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.contest.Contest;
import judgels.api.contest.ContestCreateData;
import judgels.api.lesson.Lesson;
import judgels.api.problem.Problem;
import judgels.api.user.User;
import judgels.contest.ContestClient;
import org.junit.jupiter.api.BeforeAll;
import tlx.archive.ArchiveClient;
import tlx.chapter.ChapterClient;
import tlx.chapter.ChapterLessonClient;
import tlx.chapter.ChapterProblemClient;
import tlx.course.CourseChapterClient;
import tlx.course.CourseClient;
import tlx.problemset.ProblemSetClient;
import tlx.problemset.ProblemSetProblemClient;

public abstract class BaseTrainingApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
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

    protected static String userAToken;
    protected static String userBToken;

    protected static Problem problem1;
    protected static Problem problem2;
    protected static Problem problem3;

    protected static Lesson lesson1;
    protected static Lesson lesson2;
    protected static Lesson lesson3;

    protected static Contest contest1;
    protected static Contest contest2;

    protected CourseClient courseClient = createClient(CourseClient.class);
    protected ChapterClient chapterClient = createClient(ChapterClient.class);
    protected CourseChapterClient courseChapterClient = createClient(CourseChapterClient.class);
    protected ChapterProblemClient chapterProblemClient = createClient(ChapterProblemClient.class);
    protected ChapterLessonClient chapterLessonClient = createClient(ChapterLessonClient.class);
    protected ArchiveClient archiveClient = createClient(ArchiveClient.class);
    protected ProblemSetClient problemSetClient = createClient(ProblemSetClient.class);
    protected ProblemSetProblemClient problemSetProblemClient = createClient(ProblemSetProblemClient.class);

    @BeforeAll
    static void setUpTraining() {
        userA = createUser("userA");
        userAToken = getToken(userA);

        userB = createUser("userB");
        userBToken = getToken(userB);

        webTarget = createWebTarget();

        problem1 = createProblem(adminToken, PROBLEM_1_SLUG);
        problem2 = createProblem(adminToken, PROBLEM_2_SLUG);
        problem3 = createBundleProblem(adminToken, PROBLEM_3_SLUG);

        lesson1 = createLesson(adminToken, LESSON_1_SLUG);
        lesson2 = createLesson(adminToken, LESSON_2_SLUG);
        lesson3 = createLesson(adminToken, LESSON_3_SLUG);
    }

    protected static Contest createContest(String slug) {
        return createClient(ContestClient.class).createContest(adminToken, new ContestCreateData.Builder()
                .slug(slug)
                .build());
    }
}
