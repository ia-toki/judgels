package judgels.uriel.api.contest.supervisor;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSupervisorServiceIntegrationTests extends BaseUrielServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_supervisors() {
        ContestSupervisorsUpsertResponse upsertResponse = supervisorService
                .upsertSupervisors(managerHeader, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                        .addUsernames(USER, USER_A, "bogus")
                        .addManagementPermissions(ALL)
                        .build());

        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_A);

        upsertResponse = supervisorService
                .upsertSupervisors(managerHeader, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                        .addUsernames(USER_A, USER_B)
                        .addManagementPermissions(FILE).build());

        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER_A, USER_B);

        ContestSupervisorsDeleteResponse deleteResponse = supervisorService
                .deleteSupervisors(managerHeader, contest.getJid(), ImmutableSet.of(USER, USER_B, "bogus"));

        assertThat(deleteResponse.getDeletedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_B);

        ContestSupervisorsResponse response = supervisorService
                .getSupervisors(managerHeader, contest.getJid(), empty());

        assertThat(response.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(userA.getJid()).addManagementPermissions(FILE).build(),
                new ContestSupervisor.Builder().userJid(supervisor.getJid()).build());
        assertThat(response.getProfilesMap()).containsOnlyKeys(userA.getJid(), supervisor.getJid());
    }
}
