package judgels.uriel.api.contest.manager;

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

class ContestManagerServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestManagerService managerService = createService(ContestManagerService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        ContestManagerUpsertResponse response =
                managerService.upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A));
        assertThat(response.getInsertedManagerProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(response.getAlreadyManagerProfilesMap()).isEmpty();

        response = managerService
                .upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, USER_B, "userC"));

        assertThat(response.getInsertedManagerProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(response.getInsertedManagerProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);
        assertThat(response.getAlreadyManagerProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(response.getAlreadyManagerProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        ContestManagersResponse allResponse =
                managerService.getManagers(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(allResponse.getData().getPage()).containsOnly(
                new ContestManager.Builder().userJid(USER_A_JID).build(),
                new ContestManager.Builder().userJid(USER_B_JID).build());
        assertThat(allResponse.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);

        ContestManagerDeleteResponse deleteResponse =
                managerService.deleteManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedManagerProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedManagerProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        allResponse = managerService.getManagers(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(allResponse.getData().getPage()).containsOnly(
                new ContestManager.Builder().userJid(USER_B_JID).build());
    }
}
