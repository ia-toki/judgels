package judgels.uriel.api.contest.manager;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.util.LinkedHashMap;
import java.util.Map;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_get_managers() {
        ContestManagersUpsertResponse upsertResponse = managerService
                .upsertManagers(adminHeader, contest.getJid(), ImmutableSet.of(USER, USER_A, "bogus"));

        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER, USER_A);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).isEmpty();

        upsertResponse = managerService
                .upsertManagers(adminHeader, contest.getJid(), ImmutableSet.of(USER_A, USER_B));

        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).containsOnlyKeys(USER_A);

        ContestManagersDeleteResponse deleteResponse = managerService
                .deleteManagers(adminHeader, contest.getJid(), ImmutableSet.of(USER, USER_B, "bogus"));

        assertThat(deleteResponse.getDeletedManagerProfilesMap()).containsOnlyKeys(USER, USER_B);

        Map<AuthHeader, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminHeader, true);
        canManageMap.put(managerHeader, false);

        for (AuthHeader authHeader : canManageMap.keySet()) {
            ContestManagersResponse response =
                    managerService.getManagers(authHeader, contest.getJid(), empty());

            assertThat(response.getData().getPage()).containsOnly(
                    new ContestManager.Builder().userJid(manager.getJid()).build(),
                    new ContestManager.Builder().userJid(userA.getJid()).build());
            assertThat(response.getProfilesMap()).containsOnlyKeys(manager.getJid(), userA.getJid());
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authHeader));
        }
    }
}
