package judgels.uriel.contest;

import static com.palantir.remoting.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import com.palantir.remoting.api.errors.ErrorType;
import judgels.persistence.api.Page;
import judgels.uriel.AbstractServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestContestantService;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.ContestStyle;
import org.junit.jupiter.api.Test;

class ContestContestantServiceIntegrationTests extends AbstractServiceIntegrationTests {
    private ContestService contestService = createService(ContestService.class);
    private ContestContestantService contestantService = createService(ContestContestantService.class);

    @Test void basic_flow() {
        assertThatRemoteExceptionThrownBy(() -> contestantService.getContestants("nonexistent", 1, 10))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);
        assertThatRemoteExceptionThrownBy(() -> contestantService.addContestants("nonexistent", ImmutableSet.of()))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);

        Contest contest = contestService.createContest(new ContestData.Builder()
                .name("contestA")
                .description("contest A")
                .style(ContestStyle.IOI).build());

        assertThat(contestantService.getContestants(contest.getJid(), 1, 10).getTotalData()).isEqualTo(0);

        contestantService.addContestants(contest.getJid(), ImmutableSet.of("A", "B"));

        Page<String> contestants = contestantService.getContestants(contest.getJid(), 1, 10);
        assertThat(contestants.getTotalData()).isEqualTo(2);
        assertThat(contestants.getData()).containsOnly("A", "B");
    }

}
