package judgels.uriel.contest;

import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;

    @Inject
    public ContestRoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestModuleStore moduleStore) {

        this.adminRoleDao = adminRoleDao;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
    }

    public boolean canCreateContest(String userJid) {
        return adminRoleDao.isAdmin(userJid);
    }

    public boolean canViewContest(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canStartVirtualContest(String userJid, Contest contest) {
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && contestTimer.hasBegun(contest)
                && !contestTimer.hasEnded(contest)
                && !contestTimer.hasStarted(contest, userJid);
    }
}
