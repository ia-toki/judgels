package judgels.uriel.api.contest;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class ContestServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private Contest contest;

    @Test
    void create_contest() {
        assertPermitted(createContest(adminHeader));
        assertForbidden(createContest(userHeader));
    }

    @Test
    void get_contest() {
        contest = createContestWithRoles();

        assertPermitted(getContest(of(adminHeader)));
        assertPermitted(getContest(of(managerHeader)));
        assertPermitted(getContest(of(supervisorHeader)));
        assertPermitted(getContest(of(contestantHeader)));
        assertForbidden(getContest(of(userHeader)));
        assertForbidden(getContest(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getContest(of(adminHeader)));
        assertPermitted(getContest(of(managerHeader)));
        assertPermitted(getContest(of(supervisorHeader)));
        assertPermitted(getContest(of(contestantHeader)));
        assertPermitted(getContest(of(userHeader)));
        assertPermitted(getContest(empty()));

        enableModule(contest, HIDDEN);

        assertPermitted(getContest(of(adminHeader)));
        assertPermitted(getContest(of(managerHeader)));
        assertForbidden(getContest(of(supervisorHeader)));
        assertForbidden(getContest(of(contestantHeader)));
        assertForbidden(getContest(of(userHeader)));
        assertForbidden(getContest(empty()));

        contest = createContest();

        assertPermitted(getContest(of(adminHeader)));
        assertForbidden(getContest(of(managerHeader)));
        assertForbidden(getContest(of(supervisorHeader)));
        assertForbidden(getContest(of(contestantHeader)));
        assertForbidden(getContest(of(userHeader)));
        assertForbidden(getContest(empty()));
    }

    @Test
    void update_contest() {
        contest = createContestWithRoles();

        assertPermitted(updateContest(adminHeader));
        assertPermitted(updateContest(managerHeader));
        assertForbidden(updateContest(supervisorHeader));
        assertForbidden(updateContest(contestantHeader));
        assertForbidden(updateContest(userHeader));

        contest = createContest();

        assertPermitted(updateContest(adminHeader));
        assertForbidden(updateContest(managerHeader));
        assertForbidden(updateContest(supervisorHeader));
        assertForbidden(updateContest(contestantHeader));
        assertForbidden(updateContest(userHeader));
    }

    @Test
    void start_virtual_contest() {
        contest = createContestWithRoles();

        assertForbidden(startVirtualContest(adminHeader));
        assertForbidden(startVirtualContest(managerHeader));
        assertForbidden(startVirtualContest(supervisorHeader));
        assertForbidden(startVirtualContest(contestantHeader));
        assertForbidden(startVirtualContest(userHeader));

        enableModule(contest, VIRTUAL);
        assertForbidden(startVirtualContest(contestantHeader));

        endContest(contest);
        assertForbidden(startVirtualContest(contestantHeader));

        beginContest(contest);
        assertPermitted(startVirtualContest(contestantHeader));
        assertForbidden(startVirtualContest(contestantHeader));
    }

    private ThrowingCallable createContest(AuthHeader authHeader) {
        return () -> contestService.createContest(authHeader, new ContestCreateData.Builder()
                .slug(randomString())
                .build());
    }

    private ThrowingCallable getContest(Optional<AuthHeader> authHeader) {
        return callAll(
                () -> contestService.getContest(authHeader, contest.getJid()),
                () -> contestService.getContestBySlug(authHeader, contest.getSlug()),
                () -> contestService.getContestDescription(authHeader, contest.getJid()));
    }

    private ThrowingCallable updateContest(AuthHeader authHeader) {
        ContestUpdateData data = new ContestUpdateData.Builder().build();
        ContestDescription description = new ContestDescription.Builder()
                .description("description")
                .build();
        return callAll(
                () -> contestService.updateContest(authHeader, contest.getJid(), data),
                () -> contestService.updateContestDescription(authHeader, contest.getJid(), description),
                () -> contestService.resetVirtualContest(authHeader, contest.getJid()));
    }

    private ThrowingCallable startVirtualContest(AuthHeader authHeader) {
        return () -> contestService.startVirtualContest(authHeader, contest.getJid());
    }
}
