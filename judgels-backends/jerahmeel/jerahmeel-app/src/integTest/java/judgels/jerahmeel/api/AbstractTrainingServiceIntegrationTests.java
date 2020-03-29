package judgels.jerahmeel.api;

import static judgels.jerahmeel.api.mocks.MockJophiel.mockJophiel;

import com.github.tomakehurst.wiremock.WireMockServer;
import judgels.jerahmeel.api.chapter.ChapterService;
import judgels.jerahmeel.api.course.CourseService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class AbstractTrainingServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;

    protected CourseService courseService = createService(CourseService.class);
    protected ChapterService chapterService = createService(ChapterService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
    }
}
