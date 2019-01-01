package judgels.uriel.api.contest.contestant;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import org.junit.jupiter.api.Test;

class ContestContestantServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        Contest contest = createContest("contest");
        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), REGISTRATION);
        managerService.upsertManagers(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(MANAGER));
        supervisorService.upsertSupervisors(ADMIN_HEADER, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                .addUsernames(SUPERVISOR)
                .build());

        // as manager

        ContestContestantsUpsertResponse upsertResponse =
                contestantService.upsertContestants(MANAGER_HEADER, contest.getJid(), ImmutableSet.of(USER_A));
        assertThat(upsertResponse.getInsertedContestantProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(upsertResponse.getAlreadyContestantProfilesMap()).isEmpty();

        upsertResponse = contestantService
                .upsertContestants(MANAGER_HEADER, contest.getJid(), ImmutableSet.of(USER_A, USER_B, "userC"));

        assertThat(upsertResponse.getInsertedContestantProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getInsertedContestantProfilesMap().get(USER_B).getUsername()).isEqualTo(USER_B);
        assertThat(upsertResponse.getAlreadyContestantProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(upsertResponse.getAlreadyContestantProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        ContestContestantsResponse response =
                contestantService.getContestants(ADMIN_HEADER, contest.getJid(), empty());

        List<ContestContestant> contestants = response.getData().getPage();
        assertThat(contestants).containsOnly(
                new ContestContestant.Builder().userJid(USER_A_JID).status(ContestContestantStatus.APPROVED).build(),
                new ContestContestant.Builder().userJid(USER_B_JID).status(ContestContestantStatus.APPROVED).build());
        assertThat(response.getProfilesMap().get(USER_A_JID).getUsername()).isEqualTo(USER_A);
        assertThat(response.getConfig().getCanManage()).isTrue();

        ContestContestantsDeleteResponse deleteResponse =
                contestantService.deleteContestants(ADMIN_HEADER, contest.getJid(), ImmutableSet.of(USER_A, "userC"));
        assertThat(deleteResponse.getDeletedContestantProfilesMap()).containsOnlyKeys(USER_A);
        assertThat(deleteResponse.getDeletedContestantProfilesMap().get(USER_A).getUsername()).isEqualTo(USER_A);

        // as supervisor

        assertThatRemoteExceptionThrownBy(() -> contestantService
                .upsertContestants(SUPERVISOR_HEADER, contest.getJid(), ImmutableSet.of("userC")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        assertThatRemoteExceptionThrownBy(() -> contestantService
                .deleteContestants(SUPERVISOR_HEADER, contest.getJid(), ImmutableSet.of("userC")))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        // as contestant

        contestantService.registerMyselfAsContestant(USER_A_HEADER, contest.getJid());

        assertThat(contestantService.getApprovedContestantsCount(USER_B_HEADER, contest.getJid())).isEqualTo(2);

        ApprovedContestContestantsResponse approvedResponse =
                contestantService.getApprovedContestants(USER_B_HEADER, contest.getJid());
        assertThat(approvedResponse.getData()).containsOnly(USER_A_JID, USER_B_JID);

        contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().plus(Duration.ofHours(10)))
                .build());

        contestantService.unregisterMyselfAsContestant(USER_B_HEADER, contest.getJid());

        assertThat(contestantService.getApprovedContestantsCount(USER_A_HEADER, contest.getJid())).isEqualTo(1);

        approvedResponse =
                contestantService.getApprovedContestants(USER_A_HEADER, contest.getJid());
        assertThat(approvedResponse.getData()).containsOnly(USER_A_JID);
    }
}
