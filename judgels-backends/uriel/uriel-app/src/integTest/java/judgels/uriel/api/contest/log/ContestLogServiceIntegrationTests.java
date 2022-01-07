package judgels.uriel.api.contest.log;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.clarification.ContestClarificationService;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import org.junit.jupiter.api.Test;

class ContestLogServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestLogService logService = createService(ContestLogService.class);
    private ContestProblemService problemService = createService(ContestProblemService.class);
    private ContestClarificationService clarificationService = createService(ContestClarificationService.class);

    @Test
    void end_to_end_flow() {
        Contest contest1 = createContest("contest-1");
        managerService.upsertManagers(ADMIN_HEADER, contest1.getJid(), ImmutableSet.of(MANAGER));
        supervisorService.upsertSupervisors(MANAGER_HEADER, contest1.getJid(), new ContestSupervisorUpsertData.Builder()
                .addUsernames(SUPERVISOR)
                .build());
        contestantService.upsertContestants(MANAGER_HEADER, contest1.getJid(), ImmutableSet.of(CONTESTANT));

        problemService.setProblems(MANAGER_HEADER, contest1.getJid(), ImmutableList.of(new ContestProblemData.Builder()
                .slug(PROBLEM_1_SLUG)
                .alias("A")
                .status(ContestProblemStatus.OPEN)
                .build()));

        moduleService.enableModule(MANAGER_HEADER, contest1.getJid(), ContestModuleType.CLARIFICATION);
        ContestClarification clarification = clarificationService
                .createClarification(CONTESTANT_HEADER, contest1.getJid(), new ContestClarificationData.Builder()
                .title("Title")
                .question("Question")
                .topicJid(PROBLEM_1_JID)
                .build());

        createContest("contest-2");

        // as manager

        await().atMost(3, TimeUnit.SECONDS).pollDelay(1, TimeUnit.SECONDS).until(() -> logService
                .getLogs(MANAGER_HEADER, contest1.getJid(), Optional.empty(), Optional.empty(), Optional.empty())
                .getData().getTotalCount() == 8);

        ContestLogsResponse response = logService
                .getLogs(MANAGER_HEADER, contest1.getJid(), Optional.empty(), Optional.empty(), Optional.empty());

        assertThat(response.getConfig().getUserJids()).containsExactlyInAnyOrder(
                CONTESTANT_JID,
                SUPERVISOR_JID,
                MANAGER_JID,
                ADMIN_JID);

        assertThat(response.getConfig().getProblemJids()).containsExactlyInAnyOrder(PROBLEM_1_JID);

        List<ContestLog> logs = response.getData().getPage();

        assertThat(Lists.transform(logs, ContestLog::getContestJid)).containsExactly(
                contest1.getJid(),
                contest1.getJid(),
                contest1.getJid(),
                contest1.getJid(),
                contest1.getJid(),
                contest1.getJid(),
                contest1.getJid(),
                contest1.getJid());

        assertThat(Lists.transform(logs, ContestLog::getUserJid)).containsExactly(
                CONTESTANT_JID,
                MANAGER_JID,
                MANAGER_JID,
                MANAGER_JID,
                MANAGER_JID,
                ADMIN_JID,
                ADMIN_JID,
                ADMIN_JID);

        assertThat(Lists.transform(logs, ContestLog::getEvent)).containsExactly(
                "CREATE_CLARIFICATION",
                "ENABLE_MODULE",
                "SET_PROBLEMS",
                "ADD_CONTESTANTS",
                "ADD_SUPERVISORS",
                "ADD_MANAGERS",
                "UPDATE_CONTEST",
                "CREATE_CONTEST");

        assertThat(logs.stream().map(ContestLog::getObject).collect(Collectors.toList())).containsExactly(
                Optional.of(clarification.getJid()),
                Optional.of("CLARIFICATION"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        assertThat(logs.stream().map(ContestLog::getProblemJid).collect(Collectors.toList())).containsExactly(
                Optional.of(PROBLEM_1_JID),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        response = logService.getLogs(
                MANAGER_HEADER, contest1.getJid(), Optional.of(MANAGER), Optional.empty(), Optional.empty());
        assertThat(Lists.transform(response.getData().getPage(), ContestLog::getUserJid)).containsExactly(
                MANAGER_JID,
                MANAGER_JID,
                MANAGER_JID,
                MANAGER_JID);

        response = logService.getLogs(
                MANAGER_HEADER, contest1.getJid(), Optional.empty(), Optional.of("A"), Optional.empty());
        assertThat(Lists.transform(response.getData().getPage(), ContestLog::getProblemJid)).containsExactly(
                Optional.of(PROBLEM_1_JID));

        // as supervisor

        assertThatThrownBy(() -> logService
                .getLogs(SUPERVISOR_HEADER, contest1.getJid(), Optional.empty(), Optional.empty(), Optional.empty()))
                .hasFieldOrPropertyWithValue("code", 403);
    }
}
