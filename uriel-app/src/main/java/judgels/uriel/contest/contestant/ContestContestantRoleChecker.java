package judgels.uriel.contest.contestant;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestContestantRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;

    @Inject
    public ContestContestantRoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao) {

        this.adminRoleDao = adminRoleDao;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canAddContestants(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid());
    }
}
