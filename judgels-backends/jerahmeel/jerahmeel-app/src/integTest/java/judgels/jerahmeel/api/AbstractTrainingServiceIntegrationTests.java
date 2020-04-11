package judgels.jerahmeel.api;

import static judgels.jerahmeel.api.mocks.MockJophiel.mockJophiel;
import static judgels.jerahmeel.api.mocks.MockSandalphon.mockSandalphon;

import com.github.tomakehurst.wiremock.WireMockServer;
import judgels.jerahmeel.api.chapter.ChapterService;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemService;
import judgels.jerahmeel.api.course.CourseService;
import judgels.jerahmeel.api.course.chapter.CourseChapterService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTrainingServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private static WireMockServer mockSandalphon;

    protected CourseService courseService = createService(CourseService.class);
    protected ChapterService chapterService = createService(ChapterService.class);
    protected CourseChapterService courseChapterService = createService(CourseChapterService.class);
    protected ChapterProblemService chapterProblemService = createService(ChapterProblemService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();

        mockSandalphon = mockSandalphon();
        mockSandalphon.start();
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
        mockSandalphon.shutdown();
    }
}
