package judgels.uriel.api.contest;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_JID;
import static judgels.uriel.api.mocks.MockJophiel.mockJophiel;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.mockSandalphon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import judgels.persistence.api.dump.DumpImportMode;
import judgels.uriel.api.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.announcement.ContestAnnouncement;
import judgels.uriel.api.contest.announcement.ContestAnnouncementService;
import judgels.uriel.api.contest.announcement.ContestAnnouncementStatus;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationService;
import judgels.uriel.api.contest.clarification.ContestClarificationStatus;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.contestant.ContestContestantStatus;
import judgels.uriel.api.contest.dump.ContestAnnouncementDump;
import judgels.uriel.api.contest.dump.ContestClarificationDump;
import judgels.uriel.api.contest.dump.ContestContestantDump;
import judgels.uriel.api.contest.dump.ContestDump;
import judgels.uriel.api.contest.dump.ContestDumpComponent;
import judgels.uriel.api.contest.dump.ContestManagerDump;
import judgels.uriel.api.contest.dump.ContestModuleDump;
import judgels.uriel.api.contest.dump.ContestProblemDump;
import judgels.uriel.api.contest.dump.ContestStyleDump;
import judgels.uriel.api.contest.dump.ContestSupervisorDump;
import judgels.uriel.api.contest.dump.ContestsDump;
import judgels.uriel.api.contest.dump.ExportContestsDumpData;
import judgels.uriel.api.contest.manager.ContestManager;
import judgels.uriel.api.contest.manager.ContestManagerService;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.IoiStyleModuleConfig;
import judgels.uriel.api.contest.module.ScoreboardModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.ContestSupervisorService;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ContestServiceDumpIntegrationTests extends AbstractServiceIntegrationTests  {
    private static WireMockServer mockJophiel;
    private static WireMockServer mockSandalphon;

    private ContestService contestService = createService(ContestService.class);
    private ContestModuleService contestModuleService = createService(ContestModuleService.class);
    private ContestProblemService contestProblemService = createService(ContestProblemService.class);
    private ContestContestantService contestContestantService = createService(ContestContestantService.class);
    private ContestSupervisorService contestSupervisorService = createService(ContestSupervisorService.class);
    private ContestManagerService contestManagerService = createService(ContestManagerService.class);
    private ContestAnnouncementService contestAnnouncementService = createService(ContestAnnouncementService.class);
    private ContestClarificationService contestClarificationService = createService(ContestClarificationService.class);

    private ContestsDump testImportDump = new ContestsDump.Builder()
            .addContests(new ContestDump.Builder()
                    .mode(DumpImportMode.RESTORE)
                    .slug("test-ioi")
                    .name("Test IOI Contest")
                    .beginTime(Instant.ofEpochMilli(1553040000000L))
                    .duration(Duration.ofHours(5))
                    .description("This is a test IOI contest")
                    .jid("JIDCONTtest-ioi")
                    .createdBy(ADMIN_JID)
                    .createdIp("ADMINIp")
                    .createdAt(Instant.ofEpochSecond(55))
                    .updatedBy(ADMIN_JID)
                    .updatedIp("ADMINIp")
                    .updatedAt(Instant.ofEpochSecond(77))
                    .style(new ContestStyleDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .name(ContestStyle.IOI)
                            .config(new IoiStyleModuleConfig.Builder()
                                    .usingLastAffectingPenalty(true)
                                    .build())
                            .createdBy(ADMIN_JID)
                            .createdIp("ADMINIp")
                            .createdAt(Instant.ofEpochSecond(55))
                            .updatedBy(ADMIN_JID)
                            .updatedIp("ADMINIp")
                            .updatedAt(Instant.ofEpochSecond(77))
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .name(ContestModuleType.REGISTRATION)
                            .enabled(false)
                            .createdBy(ADMIN_JID)
                            .createdIp("ADMINIp")
                            .createdAt(Instant.ofEpochSecond(56))
                            .updatedBy(MANAGER_JID)
                            .updatedIp("managerIp")
                            .updatedAt(Instant.ofEpochSecond(64))
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestModuleType.SCOREBOARD)
                            .enabled(true)
                            .config(ScoreboardModuleConfig.DEFAULT)
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestModuleType.CLARIFICATION_TIME_LIMIT)
                            .enabled(true)
                            .config(new ClarificationTimeLimitModuleConfig.Builder()
                                    .clarificationDuration(Duration.ofHours(2))
                                    .build())
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestModuleType.CLARIFICATION)
                            .enabled(true)
                            .build())
                    .addProblems(new ContestProblemDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .alias("A")
                            .problemJid(PROBLEM_1_JID)
                            .status(ContestProblemStatus.OPEN)
                            .submissionsLimit(20)
                            .points(40)
                            .createdBy(ADMIN_JID)
                            .createdIp("ADMINIp")
                            .createdAt(Instant.ofEpochSecond(56))
                            .updatedBy(MANAGER_JID)
                            .updatedIp("managerIp")
                            .updatedAt(Instant.ofEpochSecond(64))
                            .build())
                    .addProblems(new ContestProblemDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .alias("B")
                            .problemJid(PROBLEM_2_JID)
                            .status(ContestProblemStatus.CLOSED)
                            .submissionsLimit(30)
                            .points(60)
                            .build())
                    .addContestants(new ContestContestantDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .userJid(USER_A_JID)
                            .status(ContestContestantStatus.APPROVED)
                            .contestStartTime(Optional.empty())
                            .createdBy(ADMIN_JID)
                            .createdIp("ADMINIp")
                            .createdAt(Instant.ofEpochSecond(57))
                            .updatedBy(MANAGER_JID)
                            .updatedIp("managerIp")
                            .updatedAt(Instant.ofEpochSecond(68))
                            .build())
                    .addContestants(new ContestContestantDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(USER_B_JID)
                            .status(ContestContestantStatus.APPROVED)
                            .contestStartTime(Instant.ofEpochSecond(1553040000600L))
                            .build())
                    .addSupervisors(new ContestSupervisorDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(SUPERVISOR_JID)
                            .managementPermissions(ImmutableSet.of(SupervisorManagementPermission.ALL))
                            .build())
                    .addSupervisors(new ContestSupervisorDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .userJid(USER_JID)
                            .managementPermissions(ImmutableSet.of(
                                    SupervisorManagementPermission.CLARIFICATION,
                                    SupervisorManagementPermission.SCOREBOARD))
                            .createdBy(ADMIN_JID)
                            .createdIp("ADMINIp")
                            .createdAt(Instant.ofEpochSecond(56))
                            .updatedBy(MANAGER_JID)
                            .updatedIp("managerIp")
                            .updatedAt(Instant.ofEpochSecond(64))
                            .build())
                    .addManagers(new ContestManagerDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(MANAGER_JID)
                            .build())
                    .addManagers(new ContestManagerDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .userJid(ADMIN_JID)
                            .createdBy(ADMIN_JID)
                            .createdIp("ADMINIp")
                            .createdAt(Instant.ofEpochSecond(56))
                            .updatedBy(MANAGER_JID)
                            .updatedIp("managerIp")
                            .updatedAt(Instant.ofEpochSecond(64))
                            .build())
                    .addAnnouncements(new ContestAnnouncementDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .title("Ann Title 1")
                            .content("Test announcement 1 content")
                            .status(ContestAnnouncementStatus.DRAFT)
                            .build())
                    .addAnnouncements(new ContestAnnouncementDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .jid("JIDANN-test2")
                            .title("Ann Title 2")
                            .content("Test announcement 2 content")
                            .status(ContestAnnouncementStatus.PUBLISHED)
                            .createdBy(SUPERVISOR_JID)
                            .createdIp("ADMINIp")
                            .createdAt(Instant.ofEpochSecond(56))
                            .updatedBy(MANAGER_JID)
                            .updatedIp("managerIp")
                            .updatedAt(Instant.ofEpochSecond(64))
                            .build())
                    .addClarifications(new ContestClarificationDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .topicJid(PROBLEM_1_JID)
                            .title("Clarification Title 1")
                            .question("Clarification question 1")
                            .build())
                    .addClarifications(new ContestClarificationDump.Builder()
                            .mode(DumpImportMode.RESTORE)
                            .jid("JIDCLAR-2")
                            .topicJid(PROBLEM_2_JID)
                            .title("Clarification Title 2")
                            .question("Clarification question 2")
                            .answer("Answer 2")
                            .createdBy(USER_A_JID)
                            .createdIp("userAIp")
                            .createdAt(Instant.ofEpochSecond(1553040001200L))
                            .updatedBy(SUPERVISOR_JID)
                            .updatedIp("supervisorIp")
                            .updatedAt(Instant.ofEpochSecond(1553040002400L))
                            .build())
                    .build())
            .build();

    private ExportContestsDumpData testExportDumpDataWithMismatchingContestJid = new ExportContestsDumpData.Builder()
            .putContests("unknown-contest-jid", new ExportContestsDumpData.ContestDumpEntry.Builder()
                    .mode(DumpImportMode.RESTORE)
                    .addAllComponents(ImmutableSet.of(ContestDumpComponent.PROBLEMS, ContestDumpComponent.CONTESTANTS))
                    .build())
            .build();

    private ExportContestsDumpData testExportDumpDataWithCreateMode = new ExportContestsDumpData.Builder()
            .putContests("JIDCONTtest-ioi", new ExportContestsDumpData.ContestDumpEntry.Builder()
                    .mode(DumpImportMode.CREATE)
                    .addAllComponents(ImmutableSet.of(
                            ContestDumpComponent.PROBLEMS,
                            ContestDumpComponent.CONTESTANTS,
                            ContestDumpComponent.SUPERVISORS,
                            ContestDumpComponent.MANAGERS,
                            ContestDumpComponent.ANNOUNCEMENTS,
                            ContestDumpComponent.CLARIFICATIONS
                    ))
                    .build())
            .build();

    private ContestsDump testExportDumpWithCreateModeResult = new ContestsDump.Builder()
            .addContests(new ContestDump.Builder()
                    .mode(DumpImportMode.CREATE)
                    .slug("test-ioi")
                    .name("Test IOI Contest")
                    .beginTime(Instant.ofEpochMilli(1553040000000L))
                    .duration(Duration.ofHours(5))
                    .description("This is a test IOI contest")
                    .style(new ContestStyleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestStyle.IOI)
                            .config(new IoiStyleModuleConfig.Builder()
                                    .usingLastAffectingPenalty(true)
                                    .build())
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestModuleType.REGISTRATION)
                            .enabled(false)
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestModuleType.SCOREBOARD)
                            .enabled(true)
                            .config(ScoreboardModuleConfig.DEFAULT)
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestModuleType.CLARIFICATION_TIME_LIMIT)
                            .enabled(true)
                            .config(new ClarificationTimeLimitModuleConfig.Builder()
                                    .clarificationDuration(Duration.ofHours(2))
                                    .build())
                            .build())
                    .addModules(new ContestModuleDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .name(ContestModuleType.CLARIFICATION)
                            .enabled(true)
                            .build())
                    .addProblems(new ContestProblemDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .alias("A")
                            .problemJid(PROBLEM_1_JID)
                            .status(ContestProblemStatus.OPEN)
                            .submissionsLimit(20)
                            .points(40)
                            .build())
                    .addProblems(new ContestProblemDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .alias("B")
                            .problemJid(PROBLEM_2_JID)
                            .status(ContestProblemStatus.CLOSED)
                            .submissionsLimit(30)
                            .points(60)
                            .build())
                    .addContestants(new ContestContestantDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(USER_A_JID)
                            .status(ContestContestantStatus.APPROVED)
                            .contestStartTime(Optional.empty())
                            .build())
                    .addContestants(new ContestContestantDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(USER_B_JID)
                            .status(ContestContestantStatus.APPROVED)
                            .contestStartTime(Instant.ofEpochSecond(1553040000600L))
                            .build())
                    .addSupervisors(new ContestSupervisorDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(SUPERVISOR_JID)
                            .managementPermissions(ImmutableSet.of(SupervisorManagementPermission.ALL))
                            .build())
                    .addSupervisors(new ContestSupervisorDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(USER_JID)
                            .managementPermissions(ImmutableSet.of(
                                    SupervisorManagementPermission.CLARIFICATION,
                                    SupervisorManagementPermission.SCOREBOARD))
                            .build())
                    .addManagers(new ContestManagerDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(MANAGER_JID)
                            .build())
                    .addManagers(new ContestManagerDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .userJid(ADMIN_JID)
                            .build())
                    .addAnnouncements(new ContestAnnouncementDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .title("Ann Title 1")
                            .content("Test announcement 1 content")
                            .status(ContestAnnouncementStatus.DRAFT)
                            .build())
                    .addAnnouncements(new ContestAnnouncementDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .title("Ann Title 2")
                            .content("Test announcement 2 content")
                            .status(ContestAnnouncementStatus.PUBLISHED)
                            .build())
                    .addClarifications(new ContestClarificationDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .topicJid(PROBLEM_1_JID)
                            .title("Clarification Title 1")
                            .question("Clarification question 1")
                            .build())
                    .addClarifications(new ContestClarificationDump.Builder()
                            .mode(DumpImportMode.CREATE)
                            .topicJid(PROBLEM_2_JID)
                            .title("Clarification Title 2")
                            .question("Clarification question 2")
                            .answer("Answer 2")
                            .build())
                    .build())
            .build();

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

    @Test
    void end_to_end_flow() {
        testUnauthorizedImportsAndExports();
        testImport();
        testExportWithMismatchingContestJid();
        testExportWithCreateMode();
    }

    void testUnauthorizedImportsAndExports() {
        assertThatThrownBy(
                () -> contestService.importDump(USER_HEADER, testImportDump))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(
                () -> contestService.exportDump(USER_HEADER, testExportDumpDataWithMismatchingContestJid))
                .hasFieldOrPropertyWithValue("code", 403);
    }

    void testImport() {
        contestService.importDump(ADMIN_HEADER, testImportDump);

        assertThat(contestService.getContests(Optional.of(ADMIN_HEADER), Optional.empty(), Optional.empty())
                .getData().getPage())
                .hasSize(1)
                .usingElementComparatorIgnoringFields("id")
                .containsOnlyOnce(new Contest.Builder()
                        .id(0) // ignored
                        .slug("test-ioi")
                        .name("Test IOI Contest")
                        .beginTime(Instant.ofEpochMilli(1553040000000L))
                        .duration(Duration.ofHours(5))
                        .jid("JIDCONTtest-ioi")
                        .style(ContestStyle.IOI)
                        .build());

        assertThat(contestService.getContestDescription(Optional.of(ADMIN_HEADER), "JIDCONTtest-ioi")
                .getDescription())
                .isEqualTo("This is a test IOI contest");

        assertThat(contestModuleService.getModules(ADMIN_HEADER, "JIDCONTtest-ioi"))
                .hasSize(2) // only shows enabled modules except SCOREBOARD
                .containsOnlyOnce(ContestModuleType.CLARIFICATION_TIME_LIMIT)
                .containsOnlyOnce(ContestModuleType.CLARIFICATION);

        assertThat(contestModuleService.getConfig(ADMIN_HEADER, "JIDCONTtest-ioi"))
                .isEqualTo(new ContestModulesConfig.Builder()
                        .ioiStyle(new IoiStyleModuleConfig.Builder()
                                .usingLastAffectingPenalty(true)
                                .build())
                        .scoreboard(ScoreboardModuleConfig.DEFAULT)
                        .clarificationTimeLimit(new ClarificationTimeLimitModuleConfig.Builder()
                                .clarificationDuration(Duration.ofHours(2))
                                .build())
                        .build());

        assertThat(contestProblemService.getProblems(Optional.of(ADMIN_HEADER), "JIDCONTtest-ioi")
                .getData())
                .hasSize(2)
                .containsOnlyOnce(new ContestProblem.Builder()
                        .alias("A")
                        .problemJid(PROBLEM_1_JID)
                        .status(ContestProblemStatus.OPEN)
                        .submissionsLimit(20)
                        .points(40)
                        .build())
                .containsOnlyOnce(new ContestProblem.Builder()
                        .alias("B")
                        .problemJid(PROBLEM_2_JID)
                        .status(ContestProblemStatus.CLOSED)
                        .submissionsLimit(30)
                        .points(60)
                        .build());

        assertThat(contestContestantService.getContestants(ADMIN_HEADER, "JIDCONTtest-ioi", Optional.empty())
                .getData().getPage())
                .hasSize(2)
                .containsOnlyOnce(new ContestContestant.Builder()
                        .userJid(USER_A_JID)
                        .status(ContestContestantStatus.APPROVED)
                        .contestStartTime(Optional.empty())
                        .build())
                .containsOnlyOnce(new ContestContestant.Builder()
                        .userJid(USER_B_JID)
                        .status(ContestContestantStatus.APPROVED)
                        .contestStartTime(Instant.ofEpochSecond(1553040000600L))
                        .build());

        assertThat(contestSupervisorService.getSupervisors(ADMIN_HEADER, "JIDCONTtest-ioi", Optional.empty())
                .getData().getPage())
                .hasSize(2)
                .containsOnlyOnce(new ContestSupervisor.Builder()
                        .userJid(SUPERVISOR_JID)
                        .managementPermissions(ImmutableSet.of(SupervisorManagementPermission.ALL))
                        .build())
                .containsOnlyOnce(new ContestSupervisor.Builder()
                        .userJid(USER_JID)
                        .managementPermissions(ImmutableSet.of(
                                SupervisorManagementPermission.CLARIFICATION,
                                SupervisorManagementPermission.SCOREBOARD))
                        .build());

        assertThat(contestManagerService.getManagers(ADMIN_HEADER, "JIDCONTtest-ioi", Optional.empty())
                .getData().getPage())
                .hasSize(2)
                .containsOnlyOnce(new ContestManager.Builder().userJid(MANAGER_JID).build())
                .containsOnlyOnce(new ContestManager.Builder().userJid(ADMIN_JID).build());

        assertThat(contestAnnouncementService.getAnnouncements(Optional.of(ADMIN_HEADER), "JIDCONTtest-ioi",
                Optional.empty()).getData().getPage())
                .hasSize(2)
                .usingElementComparatorIgnoringFields("id")
                .containsOnlyOnce(new ContestAnnouncement.Builder()
                        .jid("JIDANN-test2")
                        .title("Ann Title 2")
                        .content("Test announcement 2 content")
                        .status(ContestAnnouncementStatus.PUBLISHED)
                        .userJid(SUPERVISOR_JID)
                        .updatedTime(Instant.ofEpochSecond(64))
                        .id(0) // ignored
                        .build())
                .usingElementComparatorIgnoringFields("id", "jid", "updatedTime")
                .containsOnlyOnce(new ContestAnnouncement.Builder()
                        .title("Ann Title 1")
                        .content("Test announcement 1 content")
                        .status(ContestAnnouncementStatus.DRAFT)
                        .userJid(ADMIN_JID)
                        .id(0) // ignored
                        .jid("JIDANN") // ignored
                        .updatedTime(Instant.ofEpochSecond(0)) // ignored
                        .build());

        assertThat(contestClarificationService.getClarifications(ADMIN_HEADER, "JIDCONTtest-ioi",
                Optional.empty(), Optional.empty(), Optional.empty()).getData().getPage())
                .hasSize(2)
                .usingElementComparatorIgnoringFields("id")
                .containsOnlyOnce(new ContestClarification.Builder()
                        .id(0) // ignored
                        .jid("JIDCLAR-2")
                        .topicJid(PROBLEM_2_JID)
                        .title("Clarification Title 2")
                        .question("Clarification question 2")
                        .answer("Answer 2")
                        .time(Instant.ofEpochSecond(1553040001200L))
                        .userJid(USER_A_JID)
                        .status(ContestClarificationStatus.ANSWERED)
                        .answererJid(SUPERVISOR_JID)
                        .answeredTime(Instant.ofEpochSecond(1553040002400L))
                        .build())
                .usingElementComparatorIgnoringFields("id", "jid", "time")
                .containsOnlyOnce(new ContestClarification.Builder()
                        .id(0) // ignored
                        .jid("") // ignored
                        .time(Instant.ofEpochSecond(0)) // ignored
                        .topicJid(PROBLEM_1_JID)
                        .title("Clarification Title 1")
                        .question("Clarification question 1")
                        .userJid(ADMIN_JID)
                        .status(ContestClarificationStatus.ASKED)
                        .build());
    }

    void testExportWithMismatchingContestJid() {
        ContestsDump dump = contestService.exportDump(ADMIN_HEADER, testExportDumpDataWithMismatchingContestJid);
        assertThat(dump).isEqualTo(new ContestsDump.Builder().build());
    }

    void testExportWithCreateMode() {
        ContestsDump dump = contestService.exportDump(ADMIN_HEADER, testExportDumpDataWithCreateMode);
        assertThat(dump).isEqualTo(testExportDumpWithCreateModeResult);
    }
}
