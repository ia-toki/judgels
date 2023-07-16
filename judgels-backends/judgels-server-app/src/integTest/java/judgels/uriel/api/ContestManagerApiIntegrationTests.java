package judgels.uriel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.manager.ContestManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_get_managers() {
        var upsertResponse = managerClient.upsertManagers(adminToken, contest.getJid(), Set.of(USER, USER_A, "bogus"));
        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER, USER_A);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).isEmpty();

        upsertResponse = managerClient.upsertManagers(adminToken, contest.getJid(), Set.of(USER_A, USER_B));
        assertThat(upsertResponse.getInsertedManagerProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getAlreadyManagerProfilesMap()).containsOnlyKeys(USER_A);

        var deleteResponse = managerClient.deleteManagers(adminToken, contest.getJid(), Set.of(USER, USER_B, "bogus"));
        assertThat(deleteResponse.getDeletedManagerProfilesMap()).containsOnlyKeys(USER, USER_B);

        Map<String, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminToken, true);
        canManageMap.put(managerToken, false);

        for (String token : canManageMap.keySet()) {
            var response = managerClient.getManagers(token, contest.getJid());
            assertThat(response.getData().getPage()).containsOnly(
                    new ContestManager.Builder().userJid(manager.getJid()).build(),
                    new ContestManager.Builder().userJid(userA.getJid()).build());
            assertThat(response.getProfilesMap()).containsOnlyKeys(manager.getJid(), userA.getJid());
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(token));
        }
    }
}
