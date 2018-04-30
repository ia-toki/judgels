package judgels.uriel.role;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.contest.supervisor.SupervisorPermissionType;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class RoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public RoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestSupervisorStore supervisorStore) {

        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
        this.supervisorStore = supervisorStore;
    }

    public boolean canCreateContest(String userJid) {
        return adminRoleDao.isAdmin(userJid);
    }

    public boolean canViewContest(String userJid, String contestJid) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contestJid);
    }

    public boolean canViewAnnouncements(String userJid, String contestJid) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contestJid);
    }

    public boolean canViewScoreboard(String userJid, String contestJid) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contestJid);
    }

    public boolean canAlsoSuperviseScoreboard(String userJid, String contestJid) {
        Optional<ContestSupervisor> supervisor = supervisorStore.findSupervisor(contestJid, userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(SupervisorPermissionType.SCOREBOARD);
    }

    public boolean canAddContestants(String userJid, String contestJid) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contestJid);
    }
}
