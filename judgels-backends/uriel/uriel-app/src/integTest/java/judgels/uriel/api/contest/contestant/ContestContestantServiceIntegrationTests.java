package judgels.uriel.api.contest.contestant;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.contestant.ContestContestantState.CONTESTANT;
import static judgels.uriel.api.contest.contestant.ContestContestantState.NONE;
import static judgels.uriel.api.contest.contestant.ContestContestantState.REGISTRABLE;
import static judgels.uriel.api.contest.contestant.ContestContestantState.REGISTRABLE_WRONG_DIVISION;
import static judgels.uriel.api.contest.contestant.ContestContestantState.REGISTRANT;
import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;
import static judgels.uriel.api.contest.module.ContestModuleType.DIVISION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_A;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_JID;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_JID;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContest()
                .managers(MANAGER)
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.CONTESTANT)
                .supervisors(SUPERVISOR_B)
                .build();
    }

    @Test
    void register_unregister_contest() {
        assertThat(contestantService.getMyContestantState(USER_HEADER, contest.getJid())).isEqualTo(NONE);
        assertThat(contestantService.getMyContestantState(USER_A_HEADER, contest.getJid())).isEqualTo(NONE);
        assertThat(contestantService.getMyContestantState(USER_B_HEADER, contest.getJid())).isEqualTo(NONE);

        enableModule(contest, REGISTRATION);
        assertThat(contestantService.getMyContestantState(USER_HEADER, contest.getJid())).isEqualTo(REGISTRABLE);
        assertThat(contestantService.getMyContestantState(USER_A_HEADER, contest.getJid())).isEqualTo(REGISTRABLE);
        assertThat(contestantService.getMyContestantState(USER_B_HEADER, contest.getJid())).isEqualTo(REGISTRABLE);

        enableModule(contest, DIVISION, new ContestModulesConfig.Builder()
                .division(new DivisionModuleConfig.Builder().division(1).build())
                .build());
        assertThat(contestantService.getMyContestantState(USER_HEADER, contest.getJid()))
                .isEqualTo(REGISTRABLE_WRONG_DIVISION);
        assertThat(contestantService.getMyContestantState(USER_A_HEADER, contest.getJid()))
                .isEqualTo(REGISTRABLE);
        assertThat(contestantService.getMyContestantState(USER_B_HEADER, contest.getJid()))
                .isEqualTo(REGISTRABLE_WRONG_DIVISION);

        contestantService.registerMyselfAsContestant(USER_A_HEADER, contest.getJid());
        assertThat(contestantService.getMyContestantState(USER_A_HEADER, contest.getJid())).isEqualTo(REGISTRANT);

        contestantService.unregisterMyselfAsContestant(USER_A_HEADER, contest.getJid());
        assertThat(contestantService.getMyContestantState(USER_A_HEADER, contest.getJid())).isEqualTo(REGISTRABLE);
    }

    @Test
    void begin_contest() {
        upsertContestants(USER);

        beginContest(contest);
        assertThat(contestantService.getMyContestantState(USER_HEADER, contest.getJid())).isEqualTo(CONTESTANT);

        ContestContestant contestant = getContestant(USER_JID);
        assertThat(contestant.getContestStartTime()).isEmpty();
    }

    @Test
    void start_reset_virtual_contest() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        upsertContestants(USER);

        beginContest(contest);
        assertThat(contestantService.getMyContestantState(USER_HEADER, contest.getJid())).isEqualTo(CONTESTANT);

        ContestContestant contestant = getContestant(USER_JID);
        assertThat(contestant.getContestStartTime()).isEmpty();

        contestService.startVirtualContest(USER_HEADER, contest.getJid());
        assertThat(contestantService.getMyContestantState(USER_HEADER, contest.getJid())).isEqualTo(CONTESTANT);

        contestant = getContestant(USER_JID);
        assertThat(contestant.getContestStartTime()).isPresent();

        contestService.resetVirtualContest(MANAGER_HEADER, contest.getJid());
        assertThat(contestantService.getMyContestantState(USER_HEADER, contest.getJid())).isEqualTo(CONTESTANT);

        contestant = getContestant(USER_JID);
        assertThat(contestant.getContestStartTime()).isEmpty();
    }

    @Test
    void upsert_delete_contestants() {
        ContestContestantsUpsertResponse upsertResponse = contestantService
                .upsertContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.of(USER, USER_A, "bogus"));

        assertThat(upsertResponse.getInsertedContestantProfilesMap()).containsOnlyKeys(USER, USER_A);
        assertThat(upsertResponse.getAlreadyContestantProfilesMap()).isEmpty();

        upsertResponse = contestantService
                .upsertContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.of(USER_A, USER_B));

        assertThat(upsertResponse.getInsertedContestantProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getAlreadyContestantProfilesMap()).containsOnlyKeys(USER_A);

        ContestContestantsDeleteResponse deleteResponse = contestantService
                .deleteContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.of(USER, USER_B, "bogus"));

        assertThat(deleteResponse.getDeletedContestantProfilesMap()).containsOnlyKeys(USER, USER_B);
    }

    @Test
    void get_contestants() {
        upsertContestants(USER, USER_A, CONTESTANT_A);
        upsertContestants(USER_A, USER_B);
        deleteContestants(USER_A, CONTESTANT_A);

        Map<AuthHeader, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(ADMIN_HEADER, true);
        canManageMap.put(MANAGER_HEADER, true);
        canManageMap.put(SUPERVISOR_A_HEADER, true);
        canManageMap.put(SUPERVISOR_B_HEADER, false);

        for (AuthHeader authHeader : canManageMap.keySet()) {
            ContestContestantsResponse response =
                    contestantService.getContestants(authHeader, contest.getJid(), empty());

            assertThat(response.getData().getPage()).containsOnly(
                    new ContestContestant.Builder().userJid(USER_JID).status(APPROVED).build(),
                    new ContestContestant.Builder().userJid(USER_B_JID).status(APPROVED).build());
            assertThat(response.getProfilesMap()).containsOnlyKeys(USER_JID, USER_B_JID);
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authHeader));
        }

        ApprovedContestContestantsResponse response =
                contestantService.getApprovedContestants(SUPERVISOR_A_HEADER, contest.getJid());

        assertThat(response.getData()).containsOnly(USER_JID, USER_B_JID);
        assertThat(response.getProfilesMap()).containsOnlyKeys(USER_JID, USER_B_JID);

        assertThat(contestantService.getApprovedContestantsCount(SUPERVISOR_A_HEADER, contest.getJid())).isEqualTo(2);
    }

    private void upsertContestants(String... usernames) {
        contestantService.upsertContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.copyOf(usernames));
    }

    private void deleteContestants(String... usernames) {
        contestantService.deleteContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.copyOf(usernames));
    }

    private List<ContestContestant> getContestants() {
        return contestantService.getContestants(SUPERVISOR_A_HEADER, contest.getJid(), empty()).getData().getPage();
    }

    private ContestContestant getContestant(String userJid) {
        ContestContestant contestant = getContestants().get(0);
        assertThat(contestant.getUserJid()).isEqualTo(userJid);
        return contestant;
    }
}
