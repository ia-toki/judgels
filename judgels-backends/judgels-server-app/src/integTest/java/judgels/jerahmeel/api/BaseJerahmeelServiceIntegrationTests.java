package judgels.jerahmeel.api;

import static judgels.jerahmeel.api.mocks.MockSandalphon.mockSandalphon;
import static judgels.jerahmeel.api.mocks.MockUriel.mockUriel;

import com.github.tomakehurst.wiremock.WireMockServer;
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
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseJerahmeelServiceIntegrationTests extends BaseJudgelsServiceIntegrationTests {
    private static WireMockServer mockSandalphon;
    private static WireMockServer mockUriel;

    protected static final String ADMIN = "admin";
    protected static final String USER = "user";
    protected static final String USER_A = "userA";
    protected static final String USER_B = "userB";

    protected static User userA;
    protected static User userB;

    protected static AuthHeader userAHeader;
    protected static AuthHeader userBHeader;

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

        mockSandalphon = mockSandalphon();
        mockSandalphon.start();

        mockUriel = mockUriel();
        mockUriel.start();
    }

    @AfterAll
    static void tearDownJerahmeel() {
        mockSandalphon.shutdown();
        mockUriel.shutdown();
    }
}
