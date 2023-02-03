package judgels.uriel.contest.submission;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.SUBMISSION;

import javax.inject.Inject;
import judgels.service.actor.Actors;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestSubmissionRoleChecker {
    private final ContestRoleChecker contestRoleChecker;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestModuleStore moduleStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestSubmissionRoleChecker(
            ContestRoleChecker contestRoleChecker,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestModuleStore moduleStore,
            ContestSupervisorStore supervisorStore) {

        this.contestRoleChecker = contestRoleChecker;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
        this.moduleStore = moduleStore;
        this.supervisorStore = supervisorStore;
    }

    public boolean canView(String userJid, Contest contest, String submissionUserJid) {
        if (canSupervise(userJid, contest)) {
            return true;
        }
        return canViewOwn(userJid, contest) && userJid.equals(submissionUserJid);
    }

    public boolean canViewOwn(String userJid, Contest contest) {
        if (canSupervise(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isContestant(userJid, contest.getJid())
                && !moduleStore.hasPausedModule(contest.getJid())
                && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canViewAll(Contest contest) {
        return contestTimer.hasEnded(contest) && contestRoleChecker.canView(Actors.GUEST, contest);
    }

    public boolean canSupervise(String userJid, Contest contest) {
        return contestRoleChecker.canSupervise(userJid, contest);
    }

    public boolean canManage(String userJid, Contest contest) {
        return contestRoleChecker.canManage(userJid, contest)
                || supervisorStore.isSupervisorWithManagementPermission(contest.getJid(), userJid, SUBMISSION);
    }
}
