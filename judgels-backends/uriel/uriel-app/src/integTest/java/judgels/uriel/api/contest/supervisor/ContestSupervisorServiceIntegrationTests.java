package judgels.uriel.api.contest.supervisor;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ALL;
import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
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

class ContestSupervisorServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");
        managerService.upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(MANAGER));

        // as manager

        ContestSupervisorsUpsertResponse upsertResponse = supervisorService.upsertSupervisors(
                ADMIN_HEADER,
                contest.getJid(),
                new ContestSupervisorUpsertData.Builder().addUsernames(USER_A).addManagementPermissions(ALL).build());
        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER_A);

        upsertResponse = supervisorService.upsertSupervisors(ADMIN_HEADER,
                contest.getJid(),
                new ContestSupervisorUpsertData.Builder()
                        .addUsernames(USER_A, USER_B, "userC")
                        .addManagementPermissions(FILE).build());

        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap()).containsOnlyKeys(USER_A, USER_B);
        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);
        assertThat(upsertResponse.getUpsertedSupervisorProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);

        ContestSupervisorsResponse managerGetSupervisorsResponse =
                supervisorService.getSupervisors(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(managerGetSupervisorsResponse.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(USER_A_JID).addManagementPermissions(FILE).build(),
                new ContestSupervisor.Builder().userJid(USER_B_JID).addManagementPermissions(FILE).build());
        assertThat(managerGetSupervisorsResponse.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);

        ContestSupervisorsDeleteResponse deleteResponse =
                supervisorService.deleteSupervisors(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedSupervisorProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedSupervisorProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        managerGetSupervisorsResponse = supervisorService.getSupervisors(ADMIN_HEADER, contest.getJid(), empty());
        assertThat(managerGetSupervisorsResponse.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(USER_B_JID).addManagementPermissions(FILE).build());

        // as supervisor

        ContestSupervisorsResponse supervisorGetSupervisorsResponse =
                supervisorService.getSupervisors(USER_B_HEADER, contest.getJid(), empty());
        assertThat(supervisorGetSupervisorsResponse.getData().getPage()).containsOnly(
                new ContestSupervisor.Builder().userJid(USER_B_JID).addManagementPermissions(FILE).build());
        assertThat(supervisorGetSupervisorsResponse.getProfilesMap().get(USER_B_JID).getUsername()).isEqualTo(USER_B);

        assertThatThrownBy(() -> supervisorService
                .upsertSupervisors(
                        USER_B_HEADER,
                        contest.getJid(),
                        new ContestSupervisorUpsertData.Builder()
                                .addUsernames(USER_A)
                                .addManagementPermissions(ALL)
                                .build()))
                .hasFieldOrPropertyWithValue("code", 403);

        assertThatThrownBy(() -> supervisorService
                .deleteSupervisors(USER_B_HEADER, contest.getJid(), ImmutableSet.of("userC")))
                .hasFieldOrPropertyWithValue("code", 403);
    }
}
