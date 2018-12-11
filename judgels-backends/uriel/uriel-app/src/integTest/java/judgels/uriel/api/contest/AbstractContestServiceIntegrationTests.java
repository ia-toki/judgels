package judgels.uriel.api.contest;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static judgels.uriel.api.mocks.MockSandalphon.mockSandalphon;
import static judgels.uriel.api.mocks.MockSealtiel.mockSealtiel;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.admin.AdminService;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.manager.ContestManagerService;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.supervisor.ContestSupervisorService;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractContestServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private static WireMockServer mockSandalphon;
    private static WireMockServer mockSealtiel;

    protected ContestService contestService = createService(ContestService.class);
    protected ContestModuleService moduleService = createService(ContestModuleService.class);
    protected ContestManagerService managerService = createService(ContestManagerService.class);
    protected ContestSupervisorService supervisorService = createService(ContestSupervisorService.class);
    protected ContestContestantService contestantService = createService(ContestContestantService.class);

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
        adminService.upsertAdmins(SUPERADMIN_HEADER, ImmutableSet.of(ADMIN));
    }

    protected Contest createContest(String slug) {
        Contest contest = contestService.createContest(
                ADMIN_HEADER,
                new ContestCreateData.Builder().slug(slug).build());
        return contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now())
                .build());
    }

    protected Contest createContestWithRoles(String slug) {
        Contest contest = createContest(slug);
        managerService.upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(MANAGER));
        supervisorService.upsertSupervisors(ADMIN_HEADER, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                .addUsernames(SUPERVISOR)
                .build());
        contestantService.upsertContestants(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(CONTESTANT));
        return contest;
    }
}
