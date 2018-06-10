package judgels.uriel.contest.problem;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.PROBLEM;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestProblemRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestProblemRoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestSupervisorStore supervisorStore) {

        this.adminRoleDao = adminRoleDao;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
        this.supervisorStore = supervisorStore;
    }

    public boolean canViewProblems(String userJid, Contest contest) {
        if (canSuperviseProblems(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canSuperviseProblems(String userJid, Contest contest) {
        return isSupervisorWithProblemPermissionOrAbove(userJid, contest);
    }

    public Optional<String> canSubmitProblem(
            String userJid,
            Contest contest,
            ContestContestantProblem contestantProblem) {

        if (!contestRoleDao.isContestant(userJid, contest.getJid())
                && !isSupervisorWithProblemPermissionOrAbove(userJid, contest)) {
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

    private boolean isSupervisorWithProblemPermissionOrAbove(String userJid, Contest contest) {
        if (adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid())) {
            return true;
        }
        Optional<ContestSupervisor> supervisor = supervisorStore.findSupervisor(contest.getJid(), userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(PROBLEM);
    }
}
