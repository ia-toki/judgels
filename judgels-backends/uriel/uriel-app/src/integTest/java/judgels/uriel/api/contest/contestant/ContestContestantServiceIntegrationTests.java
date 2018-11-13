package judgels.uriel.api.contest.contestant;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestContestantServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestContestantService contestantService = createService(ContestContestantService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        ContestContestantUpsertResponse response =
                contestantService.upsertContestants(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A));

        assertThat(response.getInsertedContestantProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(response.getAlreadyContestantProfilesMap()).isEmpty();

        response = contestantService
                .upsertContestants(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, USER_B, "userC"));

        assertThat(response.getInsertedContestantProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(response.getInsertedContestantProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);
        assertThat(response.getAlreadyContestantProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(response.getAlreadyContestantProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);
    }
}
