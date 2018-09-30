package judgels.uriel.contest.announcement;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.ANNOUNCEMENT;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestAnnouncementRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestAnnouncementRoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestSupervisorStore supervisorStore) {

        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
        this.supervisorStore = supervisorStore;
    }

    public boolean canViewAllAnnouncements(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isSupervisorOrAbove(userJid, contest.getJid());
    }

    public boolean canViewPublishedAnnouncements(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canCreateAnnouncement(String userJid, Contest contest) {
        if (adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid())) {
            return true;
        }
        Optional<ContestSupervisor> supervisor = supervisorStore.getSupervisor(contest.getJid(), userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(ANNOUNCEMENT);
    }
}
