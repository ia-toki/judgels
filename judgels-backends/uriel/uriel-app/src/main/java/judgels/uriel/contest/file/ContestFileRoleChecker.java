package judgels.uriel.contest.file;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.FILE;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;

public class ContestFileRoleChecker {
    private final ContestRoleChecker contestRoleChecker;
    private final ContestModuleStore moduleStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestFileRoleChecker(
            ContestRoleChecker contestRoleChecker,
            ContestModuleStore moduleStore,
            ContestSupervisorStore supervisorStore) {

        this.contestRoleChecker = contestRoleChecker;
        this.moduleStore = moduleStore;
        this.supervisorStore = supervisorStore;
    }

    public boolean canSupervise(String userJid, Contest contest) {
        if (!moduleStore.hasFileModule(contest.getJid())) {
            return false;
        }
        return contestRoleChecker.canSupervise(userJid, contest);
    }

    public boolean canManage(String userJid, Contest contest) {
        if (!moduleStore.hasFileModule(contest.getJid())) {
            return false;
        }
        return contestRoleChecker.canManage(userJid, contest)
                || supervisorStore.isSupervisorWithManagementPermission(contest.getJid(), userJid, FILE);
    }
}
