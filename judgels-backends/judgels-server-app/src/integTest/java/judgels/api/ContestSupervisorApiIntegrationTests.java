package judgels.api;

import static judgels.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.api.contest.supervisor.SupervisorManagementPermission.FILE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import judgels.api.contest.Contest;
import judgels.api.contest.supervisor.ContestSupervisor;
import judgels.api.contest.supervisor.ContestSupervisorsAddData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSupervisorApiIntegrationTests extends BaseContestApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void add_remove_supervisors() {
        var addResponse = supervisorClient.addSupervisors(managerToken, contest.getJid(), new ContestSupervisorsAddData.Builder()
                .addUsernames(USER, USER_A, "bogus")
                .addManagementPermissions(ALL)
                .build());
        assertThat(addResponse.getAddedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_A);

        addResponse = supervisorClient.addSupervisors(managerToken, contest.getJid(), new ContestSupervisorsAddData.Builder()
                .addUsernames(USER_A, USER_B)
                .addManagementPermissions(FILE).build());
        assertThat(addResponse.getAddedSupervisorProfilesMap()).containsOnlyKeys(USER_A, USER_B);

        var deleteResponse = supervisorClient.removeSupervisors(managerToken, contest.getJid(), Set.of(USER, USER_B, "bogus"));
        assertThat(deleteResponse.getRemovedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_B);

        var response = supervisorClient.getSupervisors(managerToken, contest.getJid());
        assertThat(response.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(userA.getJid()).addManagementPermissions(FILE).build(),
                new ContestSupervisor.Builder().userJid(supervisor.getJid()).build());
        assertThat(response.getProfilesMap()).containsOnlyKeys(userA.getJid(), supervisor.getJid());
    }
}
