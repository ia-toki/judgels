package judgels.uriel.api;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import judgels.uriel.ContestClarificationClient;
import judgels.uriel.ContestLogClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.clarification.ContestClarification;
import judgels.uriel.api.contest.clarification.ContestClarificationData;
import judgels.uriel.api.contest.log.ContestLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestLogApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestLogClient logClient = createClient(ContestLogClient.class);
    private final ContestClarificationClient clarificationClient = createClient(ContestClarificationClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .problems("A", PROBLEM_1_SLUG)
                .modules(CLARIFICATION)
                .build();
    }

    @Test
    void get_logs() {
        ContestClarification clarification = clarificationClient
                .createClarification(contestantToken, contest.getJid(), new ContestClarificationData.Builder()
                .title("Title")
                .question("Question")
                .topicJid(problem1.getJid())
                .build());

        createContest();

        await().atMost(7, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).until(() -> logClient
                .getLogs(managerToken, contest.getJid(), null)
                .getData().getTotalCount() == 8);

        var response = logClient.getLogs(managerToken, contest.getJid(), null);
        assertThat(response.getConfig().getUserJids()).containsExactlyInAnyOrder(
                contestant.getJid(),
                supervisor.getJid(),
                manager.getJid(),
                admin.getJid());
        assertThat(response.getConfig().getProblemJids()).containsExactlyInAnyOrder(problem1.getJid());

        List<ContestLog> logs = response.getData().getPage();

        assertThat(Lists.transform(logs, ContestLog::getContestJid)).containsExactly(
                contest.getJid(),
                contest.getJid(),
                contest.getJid(),
                contest.getJid(),
                contest.getJid(),
                contest.getJid(),
                contest.getJid(),
                contest.getJid());

        assertThat(Lists.transform(logs, ContestLog::getUserJid)).containsExactly(
                contestant.getJid(),
                manager.getJid(),
                manager.getJid(),
                manager.getJid(),
                manager.getJid(),
                admin.getJid(),
                admin.getJid(),
                admin.getJid());

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
                Optional.of(problem1.getJid()),
                empty(),
                empty(),
                empty(),
                empty(),
                empty(),
                empty(),
                empty());

        var params = new ContestLogClient.GetLogsParams();
        params.username = MANAGER;
        response = logClient.getLogs(managerToken, contest.getJid(), params);
        assertThat(Lists.transform(response.getData().getPage(), ContestLog::getUserJid)).containsExactly(
                manager.getJid(),
                manager.getJid(),
                manager.getJid(),
                manager.getJid());

        params = new ContestLogClient.GetLogsParams();
        params.problemAlias = "A";
        response = logClient.getLogs(managerToken, contest.getJid(), params);
        assertThat(Lists.transform(response.getData().getPage(), ContestLog::getProblemJid)).containsExactly(
                Optional.of(problem1.getJid()));
    }
}
