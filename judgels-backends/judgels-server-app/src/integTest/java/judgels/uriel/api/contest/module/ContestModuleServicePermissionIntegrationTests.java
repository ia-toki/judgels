package judgels.uriel.api.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;

import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestModuleServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void enable_disable_module_upsert_config() {
        assertPermitted(enableDisableModuleUpsertConfig(adminHeader));
        assertPermitted(enableDisableModuleUpsertConfig(managerHeader));
        assertForbidden(enableDisableModuleUpsertConfig(supervisorHeader));
        assertForbidden(enableDisableModuleUpsertConfig(contestantHeader));
        assertForbidden(enableDisableModuleUpsertConfig(userHeader));
    }

    @Test
    void get_modules() {
        assertPermitted(getModules(adminHeader));
        assertPermitted(getModules(managerHeader));
        assertPermitted(getModules(supervisorHeader));
        assertPermitted(getModules(contestantHeader));
        assertForbidden(getModules(userHeader));

        enableModule(contest, REGISTRATION);
        assertPermitted(getModules(userHeader));
    }

    @Test
    void get_config() {
        assertPermitted(getConfig(adminHeader));
        assertPermitted(getConfig(managerHeader));
        assertPermitted(getConfig(supervisorHeader));
        assertForbidden(getConfig(contestantHeader));
        assertForbidden(getConfig(userHeader));
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
