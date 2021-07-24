package judgels.uriel.api.contest;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static judgels.uriel.api.mocks.MockSandalphon.mockSandalphon;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.manager.ContestManagerService;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.supervisor.ContestSupervisorService;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractContestServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private static WireMockServer mockJophiel;
    private static WireMockServer mockSandalphon;

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
    }

    @AfterAll
    static void tearDownMocks() {
        mockJophiel.shutdown();
        mockSandalphon.shutdown();
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
