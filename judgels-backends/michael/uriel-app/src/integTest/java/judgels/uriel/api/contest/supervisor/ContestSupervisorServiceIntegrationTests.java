package judgels.uriel.api.contest.supervisor;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSupervisorServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_supervisors() {
        ContestSupervisorsUpsertResponse upsertResponse = supervisorService
                .upsertSupervisors(MANAGER_HEADER, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                        .addUsernames(USER, USER_A, "bogus")
                        .addManagementPermissions(ALL)
                        .build());

        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_A);

        upsertResponse = supervisorService
                .upsertSupervisors(MANAGER_HEADER, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                        .addUsernames(USER_A, USER_B)
                        .addManagementPermissions(FILE).build());

        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER_A, USER_B);

        ContestSupervisorsDeleteResponse deleteResponse = supervisorService
                .deleteSupervisors(MANAGER_HEADER, contest.getJid(), ImmutableSet.of(USER, USER_B, "bogus"));

        assertThat(deleteResponse.getDeletedSupervisorProfilesMap()).containsOnlyKeys(USER, USER_B);

        ContestSupervisorsResponse response = supervisorService
                .getSupervisors(MANAGER_HEADER, contest.getJid(), empty());

        assertThat(response.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(USER_A_JID).addManagementPermissions(FILE).build(),
                new ContestSupervisor.Builder().userJid(SUPERVISOR_JID).build());
        assertThat(response.getProfilesMap()).containsOnlyKeys(USER_A_JID, SUPERVISOR_JID);
    }
}
