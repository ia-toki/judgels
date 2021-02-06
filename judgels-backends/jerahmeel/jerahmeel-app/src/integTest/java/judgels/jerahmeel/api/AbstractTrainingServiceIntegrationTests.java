package judgels.jerahmeel.api;

import static judgels.jerahmeel.api.mocks.MockJophiel.mockJophiel;
import static judgels.jerahmeel.api.mocks.MockSandalphon.mockSandalphon;
import static judgels.jerahmeel.api.mocks.MockUriel.mockUriel;

import com.github.tomakehurst.wiremock.WireMockServer;
import judgels.jerahmeel.api.archive.ArchiveService;
import judgels.jerahmeel.api.chapter.ChapterService;
import judgels.jerahmeel.api.chapter.lesson.ChapterLessonService;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemService;
import judgels.jerahmeel.api.course.CourseService;
import judgels.jerahmeel.api.course.chapter.CourseChapterService;
import judgels.jerahmeel.api.problemset.ProblemSetService;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTrainingServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private static WireMockServer mockSandalphon;
    private static WireMockServer mockUriel;

    protected CourseService courseService = createService(CourseService.class);
    protected ChapterService chapterService = createService(ChapterService.class);
    protected CourseChapterService courseChapterService = createService(CourseChapterService.class);
    protected ChapterProblemService chapterProblemService = createService(ChapterProblemService.class);
    protected ChapterLessonService chapterLessonService = createService(ChapterLessonService.class);
    protected ArchiveService archiveService = createService(ArchiveService.class);
    protected ProblemSetService problemSetService = createService(ProblemSetService.class);
    protected ProblemSetProblemService problemSetProblemService = createService(ProblemSetProblemService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();

        mockSandalphon = mockSandalphon();
        mockSandalphon.start();

        mockUriel = mockUriel();
        mockUriel.start();
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
        mockSandalphon.shutdown();
        mockUriel.shutdown();
    }
}
