package judgels.contest.announcement;

import static judgels.api.contest.supervisor.SupervisorManagementPermission.ANNOUNCEMENT;

import jakarta.inject.Inject;
import judgels.api.contest.Contest;
import judgels.contest.ContestRoleChecker;
import judgels.contest.supervisor.ContestSupervisorStore;

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
