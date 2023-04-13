package judgels.uriel.api.contest.log;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_JID;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestLogServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestLogService logService = createService(ContestLogService.class);
    private final ContestClarificationService clarificationService = createService(ContestClarificationService.class);

    private Contest contest1;

    @BeforeEach
    void before() {
        contest1 = buildContestWithRoles()
                .begun()
                .problems("A", PROBLEM_1_SLUG)
                .modules(CLARIFICATION)
                .build();
    }

    @Test
    void get_logs() {
        ContestClarification clarification = clarificationService
                .createClarification(CONTESTANT_HEADER, contest1.getJid(), new ContestClarificationData.Builder()
                .title("Title")
                .question("Question")
                .topicJid(PROBLEM_1_JID)
                .build());

        createContest();

        await().atMost(3, TimeUnit.SECONDS).pollDelay(1, TimeUnit.SECONDS).until(() -> logService
                .getLogs(MANAGER_HEADER, contest1.getJid(), empty(), empty(), empty())
                .getData().getTotalCount() == 8);

        ContestLogsResponse response = logService.getLogs(MANAGER_HEADER, contest1.getJid(), empty(), empty(), empty());

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
                empty(),
                empty(),
                empty(),
                empty(),
                empty(),
                empty());

        assertThat(logs.stream().map(ContestLog::getProblemJid).collect(Collectors.toList())).containsExactly(
                Optional.of(PROBLEM_1_JID),
                empty(),
                empty(),
                empty(),
                empty(),
                empty(),
                empty(),
                empty());

        response = logService.getLogs(MANAGER_HEADER, contest1.getJid(), Optional.of(MANAGER), empty(), empty());
        assertThat(Lists.transform(response.getData().getPage(), ContestLog::getUserJid)).containsExactly(
                MANAGER_JID,
                MANAGER_JID,
                MANAGER_JID,
                MANAGER_JID);

        response = logService.getLogs(
                MANAGER_HEADER, contest1.getJid(), empty(), Optional.of("A"), empty());
        assertThat(Lists.transform(response.getData().getPage(), ContestLog::getProblemJid)).containsExactly(
                Optional.of(PROBLEM_1_JID));
    }
}
