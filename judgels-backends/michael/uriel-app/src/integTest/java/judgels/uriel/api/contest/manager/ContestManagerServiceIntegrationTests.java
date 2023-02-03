package judgels.uriel.api.contest.manager;

import static java.util.Optional.empty;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.LinkedHashMap;
import java.util.Map;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_get_managers() {
        ContestManagersUpsertResponse upsertResponse = managerService
                .upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER, USER_A, "bogus"));

        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER, USER_A);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).isEmpty();

        upsertResponse = managerService
                .upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, USER_B));

        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).containsOnlyKeys(USER_A);

        ContestManagersDeleteResponse deleteResponse = managerService
                .deleteManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER, USER_B, "bogus"));

        assertThat(deleteResponse.getDeletedManagerProfilesMap()).containsOnlyKeys(USER, USER_B);

        Map<AuthHeader, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(ADMIN_HEADER, true);
        canManageMap.put(MANAGER_HEADER, false);

        for (AuthHeader authHeader : canManageMap.keySet()) {
            ContestManagersResponse response =
                    managerService.getManagers(authHeader, contest.getJid(), empty());

            assertThat(response.getData().getPage()).containsOnly(
                    new ContestManager.Builder().userJid(MANAGER_JID).build(),
                    new ContestManager.Builder().userJid(USER_A_JID).build());
            assertThat(response.getProfilesMap()).containsOnlyKeys(MANAGER_JID, USER_A_JID);
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authHeader));
        }
    }
}
