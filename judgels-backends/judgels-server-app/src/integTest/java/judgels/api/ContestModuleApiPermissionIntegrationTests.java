package judgels.api;

import static judgels.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.api.contest.module.ContestModuleType.VIRTUAL;

import judgels.api.contest.Contest;
import judgels.api.contest.module.ContestModulesConfig;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestModuleApiPermissionIntegrationTests extends BaseContestApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void enable_disable_module_set_config() {
        assertPermitted(enableDisableModuleSetConfig(adminToken));
        assertPermitted(enableDisableModuleSetConfig(managerToken));
        assertForbidden(enableDisableModuleSetConfig(supervisorToken));
        assertForbidden(enableDisableModuleSetConfig(contestantToken));
        assertForbidden(enableDisableModuleSetConfig(userToken));
    }

    @Test
    void get_modules() {
        assertPermitted(getModules(adminToken));
        assertPermitted(getModules(managerToken));
        assertPermitted(getModules(supervisorToken));
        assertPermitted(getModules(contestantToken));
        assertForbidden(getModules(userToken));

        enableModule(contest, REGISTRATION);
        assertPermitted(getModules(userToken));
    }

    @Test
    void get_config() {
        assertPermitted(getConfig(adminToken));
        assertPermitted(getConfig(managerToken));
        assertPermitted(getConfig(supervisorToken));
        assertForbidden(getConfig(contestantToken));
        assertForbidden(getConfig(userToken));
    }

    private ThrowingCallable enableDisableModuleSetConfig(String token) {
        return callAll(
                () -> moduleClient.enableModule(token, contest.getJid(), VIRTUAL),
                () -> moduleClient.disableModule(token, contest.getJid(), VIRTUAL),
                () -> moduleClient.setModulesConfig(token, contest.getJid(), new ContestModulesConfig.Builder().build()));
    }

    private ThrowingCallable getModules(String token) {
        return () -> moduleClient.getModules(token, contest.getJid());
    }

    private ThrowingCallable getConfig(String token) {
        return () -> moduleClient.getModulesConfig(token, contest.getJid());
    }
}
