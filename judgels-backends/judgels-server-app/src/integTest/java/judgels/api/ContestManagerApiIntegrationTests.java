package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import judgels.api.contest.Contest;
import judgels.api.contest.manager.ContestManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerApiIntegrationTests extends BaseContestApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void add_remove_get_managers() {
        var addResponse = managerClient.addManagers(adminToken, contest.getJid(), Set.of(USER, USER_A, "bogus"));
        assertThat(addResponse.getAddedManagerProfilesMap()).containsOnlyKeys(USER, USER_A);
        assertThat(addResponse.getAlreadyManagerProfilesMap()).isEmpty();

        addResponse = managerClient.addManagers(adminToken, contest.getJid(), Set.of(USER_A, USER_B));
        assertThat(addResponse.getAddedManagerProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(addResponse.getAlreadyManagerProfilesMap()).containsOnlyKeys(USER_A);

        var deleteResponse = managerClient.removeManagers(adminToken, contest.getJid(), Set.of(USER, USER_B, "bogus"));
        assertThat(deleteResponse.getRemovedManagerProfilesMap()).containsOnlyKeys(USER, USER_B);

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
