package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestModuleApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void enable_disable_module_upsert_config() {
        assertPermitted(enableDisableModuleUpsertConfig(adminToken));
        assertPermitted(enableDisableModuleUpsertConfig(managerToken));
        assertForbidden(enableDisableModuleUpsertConfig(supervisorToken));
        assertForbidden(enableDisableModuleUpsertConfig(contestantToken));
        assertForbidden(enableDisableModuleUpsertConfig(userToken));
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

    private ThrowingCallable enableDisableModuleUpsertConfig(String token) {
        return callAll(
                () -> moduleClient.enableModule(token, contest.getJid(), VIRTUAL),
                () -> moduleClient.disableModule(token, contest.getJid(), VIRTUAL),
                () -> moduleClient.upsertConfig(token, contest.getJid(), new ContestModulesConfig.Builder().build()));
    }

    private ThrowingCallable getModules(String token) {
        return () -> moduleClient.getModules(token, contest.getJid());
    }

    private ThrowingCallable getConfig(String token) {
        return () -> moduleClient.getConfig(token, contest.getJid());
    }
}
