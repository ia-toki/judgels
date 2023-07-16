package judgels.uriel.api;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSupervisorApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_supervisors() {
        var upsertResponse = supervisorClient.upsertSupervisors(managerToken, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                .addUsernames(USER, USER_A, "bogus")
                .addManagementPermissions(ALL)
                .build());
        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_A);

        upsertResponse = supervisorClient.upsertSupervisors(managerToken, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                .addUsernames(USER_A, USER_B)
                .addManagementPermissions(FILE).build());
        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER_A, USER_B);

        var deleteResponse = supervisorClient.deleteSupervisors(managerToken, contest.getJid(), Set.of(USER, USER_B, "bogus"));
        assertThat(deleteResponse.getDeletedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_B);

        var response = supervisorClient.getSupervisors(managerToken, contest.getJid());
        assertThat(response.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(userA.getJid()).addManagementPermissions(FILE).build(),
                new ContestSupervisor.Builder().userJid(supervisor.getJid()).build());
        assertThat(response.getProfilesMap()).containsOnlyKeys(userA.getJid(), supervisor.getJid());
    }
}
