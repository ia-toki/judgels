package judgels.uriel.api.contest.supervisor;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;
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

class ContestSupervisorServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestSupervisorService supervisorService = createService(ContestSupervisorService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");

        ContestSupervisorUpsertResponse response = supervisorService.upsertSupervisors(
                ADMIN_HEADER,
                contest.getJid(),
                new ContestSupervisorUpsertData.Builder().addUsernames(USER_A).addManagementPermissions(ALL).build());
        assertThat(response.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER_A);

        response = supervisorService.upsertSupervisors(ADMIN_HEADER,
                contest.getJid(),
                new ContestSupervisorUpsertData.Builder()
                        .addUsernames(USER_A, USER_B, "userC")
                        .addManagementPermissions(FILE).build());

        assertThat(response.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER_A, USER_B);
        assertThat(response.getUpsertedSupervisorProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);
        assertThat(response.getUpsertedSupervisorProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);

        ContestSupervisorsResponse allResponse =
                supervisorService.getSupervisors(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(allResponse.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(USER_A_JID).addManagementPermissions(FILE).build(),
                new ContestSupervisor.Builder().userJid(USER_B_JID).addManagementPermissions(FILE).build());
        assertThat(allResponse.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);

        ContestSupervisorDeleteResponse deleteResponse =
                supervisorService.deleteSupervisors(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedSupervisorProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedSupervisorProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        allResponse = supervisorService.getSupervisors(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(allResponse.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(USER_B_JID).addManagementPermissions(FILE).build());
    }
}
