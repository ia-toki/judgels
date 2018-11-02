package judgels.uriel.api.contest.clarification;

import static java.util.Optional.empty;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.module.ContestModuleService;
import judgels.uriel.api.contest.module.ContestModuleType;
import org.junit.jupiter.api.Test;

class ContestClarificationServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestModuleService moduleService = createService(ContestModuleService.class);
    private ContestContestantService contestantService = createService(ContestContestantService.class);
    private ContestClarificationService clarificationService = createService(ContestClarificationService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.CLARIFICATION);

        contestantService.registerMyselfAsContestant(USER_A_HEADER, contest.getJid());
        contestantService.registerMyselfAsContestant(USER_B_HEADER, contest.getJid());

        clarificationService.createClarification(USER_A_HEADER, contest.getJid(), new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title("Snack")
                .question("Is snack provided?")
                .build());

        clarificationService.createClarification(USER_B_HEADER, contest.getJid(), new ContestClarificationData.Builder()
                .topicJid(contest.getJid())
                .title("Printing")
                .question("Can we print?")
                .build());

        List<ContestClarification> clarifications = clarificationService
                .getClarifications(ADMIN_HEADER, contest.getJid(), empty(), empty()).getData().getData();

        ContestClarification clarification1 = clarifications.get(0);
        ContestClarification clarification2 = clarifications.get(1);

        assertThat(clarification2.getUserJid()).isEqualTo(USER_A_JID);
        assertThat(clarification2.getTopicJid()).isEqualTo(contest.getJid());
        assertThat(clarification2.getTitle()).isEqualTo("Snack");
        assertThat(clarification2.getQuestion()).isEqualTo("Is snack provided?");
        assertThat(clarification2.getStatus()).isEqualTo(ContestClarificationStatus.ASKED);
        assertThat(clarification2.getAnswer()).isEmpty();
        assertThat(clarification2.getAnswererJid()).isEmpty();

        assertThat(clarification1.getUserJid()).isEqualTo(USER_B_JID);

        ContestClarificationAnswerData answer = new ContestClarificationAnswerData.Builder()
                .answer("Yes!")
                .build();
        clarificationService.answerClarification(ADMIN_HEADER, contest.getJid(), clarification1.getJid(), answer);

        clarifications = clarificationService
                .getClarifications(ADMIN_HEADER, contest.getJid(), empty(), empty()).getData().getData();
        clarification1 = clarifications.get(0);

        assertThat(clarification1.getAnswer()).contains("Yes!");
        assertThat(clarification1.getAnswererJid()).contains(ADMIN_JID);
    }
}
