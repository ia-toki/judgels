package judgels.uriel.contest.problem;

import static judgels.uriel.api.contest.supervisor.SupervisorManagementPermission.PROBLEM;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestProblemRoleChecker {
    private final ContestRoleChecker contestRoleChecker;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestSupervisorStore supervisorStore;
    private final ContestModuleStore moduleStore;

    @Inject
    public ContestProblemRoleChecker(
            ContestRoleChecker contestRoleChecker,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestSupervisorStore supervisorStore,
            ContestModuleStore moduleStore) {

        this.contestRoleChecker = contestRoleChecker;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
        this.supervisorStore = supervisorStore;
        this.moduleStore = moduleStore;
    }

    public boolean canView(String userJid, Contest contest) {
        if (canSupervise(userJid, contest)) {
            return true;
        }

        if (moduleStore.getVirtualModuleConfig(contest.getJid()).isPresent()) {
            boolean asContestant = contestRoleDao.isContestant(userJid, contest.getJid())
                    && contestTimer.hasStarted(contest, userJid);
            boolean asViewerOrAbove = contestRoleDao.isViewerOrAbove(userJid, contest.getJid())
                    && contestTimer.hasEnded(contest);
            return asContestant || asViewerOrAbove;
        }
        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contestTimer.hasStarted(contest, userJid);
    }

    public Optional<String> canSubmit(
            String userJid,
            Contest contest,
            ContestContestantProblem contestantProblem) {

        if (!contestRoleDao.isContestant(userJid, contest.getJid()) && !canSupervise(userJid, contest)) {
            return Optional.of("You are not a contestant.");
        }
        if (!contestTimer.hasStarted(contest, userJid)) {
            return Optional.of("Contest has not started yet.");
        }
        if (contestTimer.hasFinished(contest, userJid)) {
            return Optional.of("Contest is over.");
        }
        if (contestantProblem.getProblem().getStatus() == ContestProblemStatus.CLOSED) {
            return Optional.of("Problem is closed.");
        }
        long submissionsLimit = contestantProblem.getProblem().getSubmissionsLimit();
        if (submissionsLimit != 0 && contestantProblem.getTotalSubmissions() >= submissionsLimit) {
            return Optional.of("Submissions limit has been reached.");
        }
        return Optional.empty();
    }

    public boolean canSupervise(String userJid, Contest contest) {
        return contestRoleChecker.canSupervise(userJid, contest);
    }

    public boolean canManage(String userJid, Contest contest) {
        return contestRoleChecker.canManage(userJid, contest)
                || supervisorStore.isSupervisorWithManagementPermission(contest.getJid(), userJid, PROBLEM);
    }
}
