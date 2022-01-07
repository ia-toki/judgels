package judgels.uriel.api.contest.clarification;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_JID;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import org.junit.jupiter.api.Test;

class ContestClarificationServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestProblemService problemService = createService(ContestProblemService.class);
    private ContestClarificationService clarificationService = createService(ContestClarificationService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.CLARIFICATION);

        contestantService.registerMyselfAsContestant(USER_A_HEADER, contest.getJid());
        contestantService.registerMyselfAsContestant(USER_B_HEADER, contest.getJid());

        problemService.setProblems(ADMIN_HEADER, contest.getJid(), ImmutableList.of(new ContestProblemData.Builder()
                .alias("A")
                .slug(PROBLEM_1_SLUG)
                .status(ContestProblemStatus.OPEN)
                .submissionsLimit(0)
                .build()));

        // as contestant

        clarificationService.createClarification(USER_A_HEADER, contest.getJid(), new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title("Snack")
                .question("Is snack provided?")
                .build());

        clarificationService.createClarification(USER_B_HEADER, contest.getJid(), new ContestClarificationData.Builder()
                .topicJid(PROBLEM_1_JID)
                .title("Printing")
                .question("Can we print?")
                .build());

        ContestClarificationsResponse response = clarificationService
                .getClarifications(USER_A_HEADER, contest.getJid(), empty(), empty(), empty());

        List<ContestClarification> clarifications = response.getData().getPage();
        assertThat(clarifications.size()).isEqualTo(1);

        ContestClarification clarification = clarifications.get(0);
        String clarificationJid = clarification.getJid();

        assertThat(clarification.getUserJid()).isEqualTo(USER_A_JID);
        assertThat(clarification.getTopicJid()).isEqualTo(contest.getJid());
        assertThat(clarification.getTitle()).isEqualTo("Snack");
        assertThat(clarification.getQuestion()).isEqualTo("Is snack provided?");
        assertThat(clarification.getStatus()).isEqualTo(ContestClarificationStatus.ASKED);
        assertThat(clarification.getAnswer()).isEmpty();
        assertThat(clarification.getAnswererJid()).isEmpty();

        ContestClarificationConfig config = response.getConfig();
        assertThat(config.getProblemJids()).containsOnly(PROBLEM_1_JID);
        assertThat(config.getCanCreate()).isTrue();
        assertThat(config.getCanSupervise()).isFalse();

        // as supervisor

        response = clarificationService.getClarifications(SUPERVISOR_HEADER, contest.getJid(),
                empty(), empty(), empty());

        clarifications = response.getData().getPage();
        assertThat(clarifications.size()).isEqualTo(2);

        assertThat(ImmutableSet.of(clarifications.get(0).getUserJid(), clarifications.get(1).getUserJid()))
                .containsOnly(USER_A_JID, USER_B_JID);

        config = response.getConfig();
        assertThat(config.getProblemJids()).isEmpty();
        assertThat(config.getCanCreate()).isFalse();
        assertThat(config.getCanSupervise()).isTrue();
        assertThat(config.getCanManage()).isFalse();

        ContestClarificationAnswerData answer = new ContestClarificationAnswerData.Builder()
                .answer("Yes!")
                .build();

        assertThatThrownBy(() -> clarificationService
                .answerClarification(SUPERVISOR_HEADER, contest.getJid(), clarificationJid, answer))
                .hasFieldOrPropertyWithValue("code", 403);

        // as manager

        response = clarificationService.getClarifications(MANAGER_HEADER, contest.getJid(), empty(), empty(), empty());

        clarifications = response.getData().getPage();
        assertThat(clarifications.size()).isEqualTo(2);

        assertThat(ImmutableSet.of(clarifications.get(0).getUserJid(), clarifications.get(1).getUserJid()))
                .containsOnly(USER_A_JID, USER_B_JID);

        config = response.getConfig();
        assertThat(config.getCanCreate()).isFalse();
        assertThat(config.getCanSupervise()).isTrue();
        assertThat(config.getCanManage()).isTrue();

        clarificationService.answerClarification(MANAGER_HEADER, contest.getJid(), clarification.getJid(), answer);

        response = clarificationService
                .getClarifications(
                        MANAGER_HEADER,
                        contest.getJid(),
                        of(ContestClarificationStatus.ASKED.name()),
                        empty(),
                        empty());

        assertThat(response.getData().getPage().size()).isEqualTo(1);

        // as contestant

        clarifications = clarificationService
                .getClarifications(USER_A_HEADER, contest.getJid(), empty(), empty(), empty()).getData().getPage();
        clarification = clarifications.get(0);

        assertThat(clarification.getAnswer()).contains("Yes!");
        assertThat(clarification.getAnswererJid()).contains(MANAGER_JID);

        // as user

        moduleService.enableModule(MANAGER_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);

        assertThatThrownBy(
                () -> clarificationService.getClarifications(USER_HEADER, contest.getJid(), empty(), empty(), empty()))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(() -> clarificationService.createClarification(
                USER_HEADER,
                contest.getJid(),
                new ContestClarificationData.Builder()
                        .topicJid(contest.getJid())
                        .title("Snack")
                        .question("Is snack provided?")
                        .build()))
                .hasFieldOrPropertyWithValue("code", 403);
    }
}
