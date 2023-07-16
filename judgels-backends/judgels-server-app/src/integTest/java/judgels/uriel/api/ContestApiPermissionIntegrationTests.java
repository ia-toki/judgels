package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.ContestDescription;
import judgels.uriel.api.contest.ContestUpdateData;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class ContestApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @Test
    void create_contest() {
        assertPermitted(doCreateContest(adminToken));
        assertForbidden(doCreateContest(userToken));
    }

    @Test
    void get_contest() {
        contest = createContestWithRoles();

        assertPermitted(getContest(adminToken));
        assertPermitted(getContest(managerToken));
        assertPermitted(getContest(supervisorToken));
        assertPermitted(getContest(contestantToken));
        assertForbidden(getContest(userToken));
        assertForbidden(getContest(""));

        enableModule(contest, REGISTRATION);

        assertPermitted(getContest(adminToken));
        assertPermitted(getContest(managerToken));
        assertPermitted(getContest(supervisorToken));
        assertPermitted(getContest(contestantToken));
        assertPermitted(getContest(userToken));
        assertPermitted(getContest(""));

        enableModule(contest, HIDDEN);

        assertPermitted(getContest(adminToken));
        assertPermitted(getContest(managerToken));
        assertForbidden(getContest(supervisorToken));
        assertForbidden(getContest(contestantToken));
        assertForbidden(getContest(userToken));
        assertForbidden(getContest(""));

        contest = createContest();

        assertPermitted(getContest(adminToken));
        assertForbidden(getContest(managerToken));
        assertForbidden(getContest(supervisorToken));
        assertForbidden(getContest(contestantToken));
        assertForbidden(getContest(userToken));
        assertForbidden(getContest(""));
    }

    @Test
    void update_contest() {
        contest = createContestWithRoles();

        assertPermitted(updateContest(adminToken));
        assertPermitted(updateContest(managerToken));
        assertForbidden(updateContest(supervisorToken));
        assertForbidden(updateContest(contestantToken));
        assertForbidden(updateContest(userToken));

        contest = createContest();

        assertPermitted(updateContest(adminToken));
        assertForbidden(updateContest(managerToken));
        assertForbidden(updateContest(supervisorToken));
        assertForbidden(updateContest(contestantToken));
        assertForbidden(updateContest(userToken));
    }

    @Test
    void start_virtual_contest() {
        contest = createContestWithRoles();

        assertForbidden(startVirtualContest(adminToken));
        assertForbidden(startVirtualContest(managerToken));
        assertForbidden(startVirtualContest(supervisorToken));
        assertForbidden(startVirtualContest(contestantToken));
        assertForbidden(startVirtualContest(userToken));

        enableModule(contest, VIRTUAL);
        assertForbidden(startVirtualContest(contestantToken));

        endContest(contest);
        assertForbidden(startVirtualContest(contestantToken));

        beginContest(contest);
        assertPermitted(startVirtualContest(contestantToken));
        assertForbidden(startVirtualContest(contestantToken));
    }

    private ThrowingCallable doCreateContest(String token) {
        return () -> contestClient.createContest(token, new ContestCreateData.Builder()
                .slug(randomString())
                .build());
    }

    private ThrowingCallable getContest(String token) {
        return callAll(
                () -> contestClient.getContest(token, contest.getJid()),
                () -> contestClient.getContestBySlug(token, contest.getSlug()),
                () -> contestClient.getContestDescription(token, contest.getJid()));
    }

    private ThrowingCallable updateContest(String token) {
        ContestUpdateData data = new ContestUpdateData.Builder().build();
        ContestDescription description = new ContestDescription.Builder()
                .description("description")
                .build();
        return callAll(
                () -> contestClient.updateContest(token, contest.getJid(), data),
                () -> contestClient.updateContestDescription(token, contest.getJid(), description),
                () -> contestClient.resetVirtualContest(token, contest.getJid()));
    }

    private ThrowingCallable startVirtualContest(String token) {
        return () -> contestClient.startVirtualContest(token, contest.getJid());
    }
}
