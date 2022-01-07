package judgels.uriel.api.contest.manager;

import static java.util.Optional.empty;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableSet;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestManagerServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        // as admin

        ContestManagersUpsertResponse upsertResponse =
                managerService.upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A));
        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).isEmpty();

        upsertResponse = managerService
                .upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, USER_B, "userC"));

        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getInsertedManagerProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        ContestManagersResponse response =
                managerService.getManagers(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(response.getData().getPage()).containsOnly(
                new ContestManager.Builder().userJid(USER_A_JID).build(),
                new ContestManager.Builder().userJid(USER_B_JID).build());
        assertThat(response.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);
        assertThat(response.getConfig().getCanManage()).isTrue();

        ContestManagersDeleteResponse deleteResponse =
                managerService.deleteManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedManagerProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedManagerProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        response = managerService.getManagers(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(response.getData().getPage()).containsOnly(
                new ContestManager.Builder().userJid(USER_B_JID).build());

        // as manager

        response = managerService.getManagers(USER_B_HEADER, contest.getJid(), empty());
        assertThat(response.getData().getPage()).containsOnly(
                new ContestManager.Builder().userJid(USER_B_JID).build());
        assertThat(response.getConfig().getCanManage()).isFalse();

        assertThatThrownBy(() -> managerService
                .upsertManagers(USER_B_HEADER, contest.getJid(), ImmutableSet.of("userC")))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(() -> managerService
                .deleteManagers(USER_B_HEADER, contest.getJid(), ImmutableSet.of("userC")))
                .hasFieldOrPropertyWithValue("code", 403);
    }
}
