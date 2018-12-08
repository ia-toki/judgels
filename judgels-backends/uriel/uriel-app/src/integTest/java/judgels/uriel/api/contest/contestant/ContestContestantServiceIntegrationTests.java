package judgels.uriel.api.contest.contestant;

import static java.util.Optional.empty;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
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

        ApprovedContestContestantsResponse approvedResponse =
                contestantService.getApprovedContestants(ADMIN_HEADER, contest.getJid());
        assertThat(approvedResponse.getData()).containsOnly(USER_A_JID, USER_B_JID);
        assertThat(approvedResponse.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);

        ContestContestantsResponse allResponse =
                contestantService.getContestants(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(allResponse.getData().getPage()).containsOnly(
                new ContestContestant.Builder().userJid(USER_A_JID).status(ContestContestantStatus.APPROVED).build(),
                new ContestContestant.Builder().userJid(USER_B_JID).status(ContestContestantStatus.APPROVED).build());
        assertThat(allResponse.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);
        assertThat(allResponse.getConfig().getCanManage()).isTrue();

        ContestContestantDeleteResponse deleteResponse =
                contestantService.deleteContestants(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedContestantProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedContestantProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        approvedResponse =
                contestantService.getApprovedContestants(ADMIN_HEADER, contest.getJid());
        assertThat(approvedResponse.getData()).containsOnly(USER_B_JID);
    }
}
