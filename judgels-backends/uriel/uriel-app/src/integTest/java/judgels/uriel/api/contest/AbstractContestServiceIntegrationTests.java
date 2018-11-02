package judgels.uriel.api.contest;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static judgels.uriel.api.mocks.MockSandalphon.mockSandalphon;
import static judgels.uriel.api.mocks.MockSealtiel.mockSealtiel;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.Instant;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.admin.AdminService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractContestServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private static WireMockServer mockSandalphon;
    private static WireMockServer mockSealtiel;

    protected ContestService contestService = createService(ContestService.class);

    @BeforeAll
    static void setUpMocks() {
        mockJophiel = mockJophiel();
        mockJophiel.start();
        mockSandalphon = mockSandalphon();
        mockSandalphon.start();
        mockSealtiel = mockSealtiel();
        mockSealtiel.start();
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
        mockSandalphon.shutdown();
        mockSealtiel.shutdown();
    }

    @BeforeEach
    void setUpAdmin() {
        AdminService adminService = createService(AdminService.class);
        adminService.upsertAdmin(SUPERADMIN_HEADER, ADMIN);
    }

    protected Contest createContest(String slug) {
        Contest contest = contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug(slug).build());
        contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now())
                .build());
        return contest;
    }
}
