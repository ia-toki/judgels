package judgels.uriel.api;

import static judgels.uriel.api.contest.contestant.ContestContestantState.NONE;
import static judgels.uriel.api.contest.contestant.ContestContestantState.REGISTRABLE;
import static judgels.uriel.api.contest.contestant.ContestContestantState.REGISTRABLE_WRONG_DIVISION;
import static judgels.uriel.api.contest.contestant.ContestContestantState.REGISTRANT;
import static judgels.uriel.api.contest.contestant.ContestContestantStatus.APPROVED;
import static judgels.uriel.api.contest.module.ContestModuleType.DIVISION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantApiIntegrationTests extends BaseUrielApiIntegrationTests {
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
        assertThat(contestantClient.getMyContestantState(userToken, contest.getJid())).isEqualTo(NONE);
        assertThat(contestantClient.getMyContestantState(userAToken, contest.getJid())).isEqualTo(NONE);
        assertThat(contestantClient.getMyContestantState(userBToken, contest.getJid())).isEqualTo(NONE);

        enableModule(contest, REGISTRATION);
        assertThat(contestantClient.getMyContestantState(userToken, contest.getJid())).isEqualTo(REGISTRABLE);
        assertThat(contestantClient.getMyContestantState(userAToken, contest.getJid())).isEqualTo(REGISTRABLE);
        assertThat(contestantClient.getMyContestantState(userBToken, contest.getJid())).isEqualTo(REGISTRABLE);

        enableModule(contest, DIVISION, new ContestModulesConfig.Builder()
                .division(new DivisionModuleConfig.Builder().division(1).build())
                .build());
        assertThat(contestantClient.getMyContestantState(userToken, contest.getJid()))
                .isEqualTo(REGISTRABLE_WRONG_DIVISION);
        assertThat(contestantClient.getMyContestantState(userAToken, contest.getJid()))
                .isEqualTo(REGISTRABLE);
        assertThat(contestantClient.getMyContestantState(userBToken, contest.getJid()))
                .isEqualTo(REGISTRABLE_WRONG_DIVISION);

        contestantClient.registerMyselfAsContestant(userAToken, contest.getJid());
        assertThat(contestantClient.getMyContestantState(userAToken, contest.getJid())).isEqualTo(REGISTRANT);

        contestantClient.unregisterMyselfAsContestant(userAToken, contest.getJid());
        assertThat(contestantClient.getMyContestantState(userAToken, contest.getJid())).isEqualTo(REGISTRABLE);
    }

    @Test
    void begin_contest() {
        upsertContestants(USER);

        beginContest(contest);
        assertThat(contestantClient.getMyContestantState(userToken, contest.getJid())).isEqualTo(ContestContestantState.CONTESTANT);

        ContestContestant contestant = getContestant(user.getJid());
        assertThat(contestant.getContestStartTime()).isEmpty();
    }

    @Test
    void start_reset_virtual_contest() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        upsertContestants(USER);

        beginContest(contest);
        assertThat(contestantClient.getMyContestantState(userToken, contest.getJid())).isEqualTo(ContestContestantState.CONTESTANT);

        ContestContestant contestant = getContestant(user.getJid());
        assertThat(contestant.getContestStartTime()).isEmpty();

        contestClient.startVirtualContest(userToken, contest.getJid());
        assertThat(contestantClient.getMyContestantState(userToken, contest.getJid())).isEqualTo(ContestContestantState.CONTESTANT);

        contestant = getContestant(user.getJid());
        assertThat(contestant.getContestStartTime()).isPresent();

        contestClient.resetVirtualContest(managerToken, contest.getJid());
        assertThat(contestantClient.getMyContestantState(userToken, contest.getJid())).isEqualTo(ContestContestantState.CONTESTANT);

        contestant = getContestant(user.getJid());
        assertThat(contestant.getContestStartTime()).isEmpty();
    }

    @Test
    void upsert_delete_contestants() {
        var upsertResponse = contestantClient.upsertContestants(supervisorAToken, contest.getJid(), Set.of(USER, USER_A, "bogus"));
        assertThat(upsertResponse.getInsertedContestantProfilesMap()).containsOnlyKeys(USER, USER_A);
        assertThat(upsertResponse.getAlreadyContestantProfilesMap()).isEmpty();

        upsertResponse = contestantClient.upsertContestants(supervisorAToken, contest.getJid(), Set.of(USER_A, USER_B));
        assertThat(upsertResponse.getInsertedContestantProfilesMap()).containsOnlyKeys(USER_B);
        assertThat(upsertResponse.getAlreadyContestantProfilesMap()).containsOnlyKeys(USER_A);

        var deleteResponse = contestantClient.deleteContestants(supervisorAToken, contest.getJid(), Set.of(USER, USER_B, "bogus"));
        assertThat(deleteResponse.getDeletedContestantProfilesMap()).containsOnlyKeys(USER, USER_B);
    }

    @Test
    void get_contestants() {
        upsertContestants(USER, USER_A, CONTESTANT_A);
        upsertContestants(USER_A, USER_B);
        deleteContestants(USER_A, CONTESTANT_A);

        Map<String, Boolean> canManageMap = new LinkedHashMap<>();
        canManageMap.put(adminToken, true);
        canManageMap.put(managerToken, true);
        canManageMap.put(supervisorAToken, true);
        canManageMap.put(supervisorBToken, false);

        for (String authToken : canManageMap.keySet()) {
            var response = contestantClient.getContestants(authToken, contest.getJid());
            assertThat(response.getData().getPage()).containsOnly(
                    new ContestContestant.Builder().userJid(user.getJid()).status(APPROVED).build(),
                    new ContestContestant.Builder().userJid(userB.getJid()).status(APPROVED).build());
            assertThat(response.getProfilesMap()).containsOnlyKeys(user.getJid(), userB.getJid());
            assertThat(response.getConfig().getCanManage()).isEqualTo(canManageMap.get(authToken));
        }

        var response = contestantClient.getApprovedContestants(supervisorAToken, contest.getJid());
        assertThat(response.getData()).containsOnly(user.getJid(), userB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(user.getJid(), userB.getJid());

        assertThat(contestantClient.getApprovedContestantsCount(supervisorAToken, contest.getJid())).isEqualTo(2);
    }

    private void upsertContestants(String... usernames) {
        contestantClient.upsertContestants(supervisorAToken, contest.getJid(), Set.of(usernames));
    }

    private void deleteContestants(String... usernames) {
        contestantClient.deleteContestants(supervisorAToken, contest.getJid(), Set.of(usernames));
    }

    private List<ContestContestant> getContestants() {
        return contestantClient.getContestants(supervisorAToken, contest.getJid()).getData().getPage();
    }

    private ContestContestant getContestant(String userJid) {
        ContestContestant contestant = getContestants().get(0);
        assertThat(contestant.getUserJid()).isEqualTo(userJid);
        return contestant;
    }
}
