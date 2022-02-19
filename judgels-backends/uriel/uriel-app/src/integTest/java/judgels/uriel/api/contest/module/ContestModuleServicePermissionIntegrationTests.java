package judgels.uriel.api.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;

import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestModuleServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void enable_disable_module_upsert_config() {
        assertPermitted(enableDisableModuleUpsertConfig(ADMIN_HEADER));
        assertPermitted(enableDisableModuleUpsertConfig(MANAGER_HEADER));
        assertForbidden(enableDisableModuleUpsertConfig(SUPERVISOR_HEADER));
        assertForbidden(enableDisableModuleUpsertConfig(CONTESTANT_HEADER));
        assertForbidden(enableDisableModuleUpsertConfig(USER_HEADER));
    }

    @Test
    void get_modules() {
        assertPermitted(getModules(ADMIN_HEADER));
        assertPermitted(getModules(MANAGER_HEADER));
        assertPermitted(getModules(SUPERVISOR_HEADER));
        assertPermitted(getModules(CONTESTANT_HEADER));
        assertForbidden(getModules(USER_HEADER));

        enableModule(contest, REGISTRATION);
        assertPermitted(getModules(USER_HEADER));
    }

    @Test
    void get_config() {
        assertPermitted(getConfig(ADMIN_HEADER));
        assertPermitted(getConfig(MANAGER_HEADER));
        assertPermitted(getConfig(SUPERVISOR_HEADER));
        assertForbidden(getConfig(CONTESTANT_HEADER));
        assertForbidden(getConfig(USER_HEADER));
    }

    private ThrowingCallable enableDisableModuleUpsertConfig(AuthHeader authHeader) {
        return callAll(
                () -> moduleService.enableModule(authHeader, contest.getJid(), VIRTUAL),
                () -> moduleService.disableModule(authHeader, contest.getJid(), VIRTUAL),
                () -> moduleService.upsertConfig(authHeader, contest.getJid(), new ContestModulesConfig.Builder()
                        .build()));
    }

    private ThrowingCallable getModules(AuthHeader authHeader) {
        return () -> moduleService.getModules(authHeader, contest.getJid());
    }

    private ThrowingCallable getConfig(AuthHeader authHeader) {
        return () -> moduleService.getConfig(authHeader, contest.getJid());
    }
}
