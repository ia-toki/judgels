package judgels.uriel.contest.announcement;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.ANNOUNCEMENT;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;

public class ContestAnnouncementRoleChecker {
    private final ContestRoleChecker contestRoleChecker;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestAnnouncementRoleChecker(
            ContestRoleChecker contestRoleChecker,
            ContestSupervisorStore supervisorStore) {

        this.contestRoleChecker = contestRoleChecker;
        this.supervisorStore = supervisorStore;
    }

    public boolean canViewPublished(String userJid, Contest contest) {
        return contestRoleChecker.canView(userJid, contest);
    }

    public boolean canSupervise(String userJid, Contest contest) {
        return contestRoleChecker.canSupervise(userJid, contest);
    }

    public boolean canManage(String userJid, Contest contest) {
        return contestRoleChecker.canManage(userJid, contest)
                || supervisorStore.isSupervisorWithManagementPermission(contest.getJid(), userJid, ANNOUNCEMENT);
    }
}
