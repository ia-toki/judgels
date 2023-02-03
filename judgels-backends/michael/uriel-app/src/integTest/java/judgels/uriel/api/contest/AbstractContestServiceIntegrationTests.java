package judgels.uriel.api.contest;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static judgels.uriel.api.mocks.MockSandalphon.mockSandalphon;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.manager.ContestManagerService;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.ContestSupervisorService;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
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
    protected ContestProblemService problemService = createService(ContestProblemService.class);

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

    protected Contest createContest() {
        return contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder()
                .slug(randomString())
                .build());
    }

    protected Contest createContest(String slug) {
        Contest contest = contestService.createContest(ADMIN_HEADER, new ContestCreateData.Builder()
                .slug(slug)
                .build());
        beginContest(contest);
        return contest;
    }

    protected Contest beginContest(Contest contest) {
        return contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now())
                .build());
    }

    protected Contest endContest(Contest contest) {
        return contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().minus(Duration.ofHours(3)))
                .duration(Duration.ofHours(3).minus(Duration.ofSeconds(1)))
                .build());
    }

    protected Contest createContestWithRoles() {
        return buildContestWithRoles()
                .build();
    }

    protected Contest enableModule(Contest contest, ContestModuleType type) {
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), type);
        return contest;
    }

    protected Contest enableModule(Contest contest, ContestModuleType type, ContestModulesConfig config) {
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), type);
        moduleService.upsertConfig(ADMIN_HEADER, contest.getJid(), config);
        return contest;
    }

    protected Contest disableModule(Contest contest, ContestModuleType type) {
        moduleService.disableModule(ADMIN_HEADER, contest.getJid(), type);
        return contest;
    }

    protected ContestBuilder buildContest() {
        return new ContestBuilder();
    }

    protected ContestBuilder buildContestWithRoles() {
        return new ContestBuilder()
                .managers(MANAGER)
                .supervisors(SUPERVISOR)
                .contestants(CONTESTANT);
    }

    protected class ContestBuilder {
        Optional<Instant> beginTime = Optional.empty();
        Optional<Duration> duration = Optional.empty();
        Optional<ContestStyle> style = Optional.empty();

        Set<ContestModuleType> modules = Collections.emptySet();
        Set<String> managers = Collections.emptySet();
        Set<String> supervisors = Collections.emptySet();
        Map<String, SupervisorManagementPermission> supervisorsWithManagementPermissions = Collections.emptyMap();
        Set<String> contestants = Collections.emptySet();
        List<String> problems = Collections.emptyList();

        public ContestBuilder beginTime(Instant instant) {
            this.beginTime = Optional.of(instant);
            return this;
        }

        public ContestBuilder duration(Duration duration) {
            this.duration = Optional.of(duration);
            return this;
        }

        public ContestBuilder style(ContestStyle style) {
            this.style = Optional.of(style);
            return this;
        }

        public ContestBuilder begun() {
            this.beginTime = Optional.of(Instant.now().minus(Duration.ofSeconds(1)));
            return this;
        }

        public ContestBuilder ended() {
            this.beginTime = Optional.of(Instant.now().minus(Duration.ofHours(10)));
            return this;
        }

        public ContestBuilder modules(ContestModuleType... types) {
            this.modules = ImmutableSet.copyOf(types);
            return this;
        }

        public ContestBuilder managers(String... usernames) {
            this.managers = ImmutableSet.copyOf(usernames);
            return this;
        }

        public ContestBuilder supervisors(String... usernames) {
            this.supervisors = ImmutableSet.copyOf(usernames);
            return this;
        }

        public ContestBuilder supervisorWithManagementPermissions(
                String supervisor,
                SupervisorManagementPermission permission) {
            this.supervisorsWithManagementPermissions = ImmutableMap.of(supervisor, permission);
            return this;
        }

        public ContestBuilder contestants(String... usernames) {
            this.contestants = ImmutableSet.copyOf(usernames);
            return this;
        }

        public ContestBuilder problems(String... aliasAndSlugs) {
            this.problems = ImmutableList.copyOf(aliasAndSlugs);
            return this;
        }

        public Contest build() {
            Contest contest = createContest();

            if (beginTime.isPresent() || duration.isPresent() || style.isPresent()) {
                ContestUpdateData data = new ContestUpdateData.Builder()
                        .style(style)
                        .beginTime(beginTime)
                        .duration(duration)
                        .build();
                contest = contestService.updateContest(ADMIN_HEADER, contest.getJid(), data);
            }

            AuthHeader managerHeader = ADMIN_HEADER;

            if (!managers.isEmpty()) {
                managerService.upsertManagers(ADMIN_HEADER, contest.getJid(), managers);
                managerHeader = MANAGER_HEADER;
            }

            if (!supervisors.isEmpty()) {
                ContestSupervisorUpsertData data = new ContestSupervisorUpsertData.Builder()
                        .usernames(supervisors)
                        .build();
                supervisorService.upsertSupervisors(managerHeader, contest.getJid(), data);
            }

            if (!supervisorsWithManagementPermissions.isEmpty()) {
                for (String supervisor : supervisorsWithManagementPermissions.keySet()) {
                    ContestSupervisorUpsertData data = new ContestSupervisorUpsertData.Builder()
                            .addUsernames(supervisor)
                            .addManagementPermissions(supervisorsWithManagementPermissions.get(supervisor))
                            .build();
                    supervisorService.upsertSupervisors(managerHeader, contest.getJid(), data);
                }
            }

            if (!contestants.isEmpty()) {
                contestantService.upsertContestants(managerHeader, contest.getJid(), contestants);
            }

            if (!problems.isEmpty()) {
                List<ContestProblemData> data = new ArrayList<>();
                for (int i = 0; i < problems.size(); i += 2) {
                    data.add(new ContestProblemData.Builder()
                            .alias(problems.get(i))
                            .slug(problems.get(i + 1))
                            .status(ContestProblemStatus.OPEN)
                            .build());
                }
                problemService.setProblems(managerHeader, contest.getJid(), data);
            }

            for (ContestModuleType module : modules) {
                moduleService.enableModule(managerHeader, contest.getJid(), module);
            }

            return contest;
        }
    }
}
