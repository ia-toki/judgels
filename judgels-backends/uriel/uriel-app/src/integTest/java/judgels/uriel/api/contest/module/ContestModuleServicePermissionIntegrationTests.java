package judgels.uriel.api.contest.module;

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
    void get_modules_get_config() {
        assertPermitted(getModulesGetConfig(ADMIN_HEADER));
        assertPermitted(getModulesGetConfig(MANAGER_HEADER));
        assertPermitted(getModulesGetConfig(SUPERVISOR_HEADER));
        assertPermitted(getModulesGetConfig(CONTESTANT_HEADER));
        assertForbidden(getModulesGetConfig(USER_HEADER));
    }

    private ThrowingCallable enableDisableModuleUpsertConfig(AuthHeader authHeader) {
        return callAll(
                () -> moduleService.enableModule(authHeader, contest.getJid(), VIRTUAL),
                () -> moduleService.disableModule(authHeader, contest.getJid(), VIRTUAL),
                () -> moduleService.upsertConfig(authHeader, contest.getJid(), new ContestModulesConfig.Builder()
                        .build()));
    }

    private ThrowingCallable getModulesGetConfig(AuthHeader authHeader) {
        return callAll(
                () -> moduleService.getModules(authHeader, contest.getJid()),
                () -> moduleService.getConfig(authHeader, contest.getJid()));
    }
}
