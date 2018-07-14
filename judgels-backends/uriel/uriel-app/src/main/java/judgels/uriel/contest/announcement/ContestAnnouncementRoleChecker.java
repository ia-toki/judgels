package judgels.uriel.contest.announcement;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestAnnouncementRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;

    @Inject
    public ContestAnnouncementRoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao) {

        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canViewPublishedAnnouncements(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }
}
