package judgels.uriel.api.contest;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;

import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class ContestServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @Test
    void create_contest() {
        assertPermitted(createContest(ADMIN_HEADER));
        assertForbidden(createContest(USER_HEADER));
    }

    @Test
    void get_contest() {
        contest = createContestWithRoles();

        assertPermitted(getContest(of(ADMIN_HEADER)));
        assertPermitted(getContest(of(MANAGER_HEADER)));
        assertPermitted(getContest(of(SUPERVISOR_HEADER)));
        assertPermitted(getContest(of(CONTESTANT_HEADER)));
        assertForbidden(getContest(of(USER_HEADER)));
        assertForbidden(getContest(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getContest(of(ADMIN_HEADER)));
        assertPermitted(getContest(of(MANAGER_HEADER)));
        assertPermitted(getContest(of(SUPERVISOR_HEADER)));
        assertPermitted(getContest(of(CONTESTANT_HEADER)));
        assertPermitted(getContest(of(USER_HEADER)));
        assertPermitted(getContest(empty()));

        enableModule(contest, HIDDEN);

        assertPermitted(getContest(of(ADMIN_HEADER)));
        assertPermitted(getContest(of(MANAGER_HEADER)));
        assertForbidden(getContest(of(SUPERVISOR_HEADER)));
        assertForbidden(getContest(of(CONTESTANT_HEADER)));
        assertForbidden(getContest(of(USER_HEADER)));
        assertForbidden(getContest(empty()));

        contest = createContest();

        assertPermitted(getContest(of(ADMIN_HEADER)));
        assertForbidden(getContest(of(MANAGER_HEADER)));
        assertForbidden(getContest(of(SUPERVISOR_HEADER)));
        assertForbidden(getContest(of(CONTESTANT_HEADER)));
        assertForbidden(getContest(of(USER_HEADER)));
        assertForbidden(getContest(empty()));
    }

    @Test
    void update_contest() {
        contest = createContestWithRoles();

        assertPermitted(updateContest(ADMIN_HEADER));
        assertPermitted(updateContest(MANAGER_HEADER));
        assertForbidden(updateContest(SUPERVISOR_HEADER));
        assertForbidden(updateContest(CONTESTANT_HEADER));
        assertForbidden(updateContest(USER_HEADER));

        contest = createContest();

        assertPermitted(updateContest(ADMIN_HEADER));
        assertForbidden(updateContest(MANAGER_HEADER));
        assertForbidden(updateContest(SUPERVISOR_HEADER));
        assertForbidden(updateContest(CONTESTANT_HEADER));
        assertForbidden(updateContest(USER_HEADER));
    }

    @Test
    void start_virtual_contest() {
        contest = createContestWithRoles();

        assertForbidden(startVirtualContest(ADMIN_HEADER));
        assertForbidden(startVirtualContest(MANAGER_HEADER));
        assertForbidden(startVirtualContest(SUPERVISOR_HEADER));
        assertForbidden(startVirtualContest(CONTESTANT_HEADER));
        assertForbidden(startVirtualContest(USER_HEADER));

        enableModule(contest, VIRTUAL);
        assertForbidden(startVirtualContest(CONTESTANT_HEADER));

        endContest(contest);
        assertForbidden(startVirtualContest(CONTESTANT_HEADER));

        beginContest(contest);
        assertPermitted(startVirtualContest(CONTESTANT_HEADER));
        assertForbidden(startVirtualContest(CONTESTANT_HEADER));
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
