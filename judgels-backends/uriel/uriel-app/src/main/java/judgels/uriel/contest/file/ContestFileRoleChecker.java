package judgels.uriel.contest.file;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestFileRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;

    @Inject
    public ContestFileRoleChecker(AdminRoleDao adminRoleDao, ContestRoleDao contestRoleDao) {
        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canDownloadFiles(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }
}
