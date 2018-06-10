package judgels.uriel.role;

import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.PROBLEM;
import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.SCOREBOARD;
import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.SUBMISSION;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.problem.ContestContestantProblem;
import judgels.uriel.api.contest.problem.ContestProblemStatus;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.SupervisorPermissionType;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class RoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public RoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestSupervisorStore supervisorStore) {

        this.adminRoleDao = adminRoleDao;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
        this.supervisorStore = supervisorStore;
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

    public boolean canViewPublishedAnnouncements(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isViewerOrAbove(userJid, contest.getJid());
    }

    public boolean canViewOwnClarifications(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isContestantOrAbove(userJid, contest.getJid());
    }

    public boolean canViewProblems(String userJid, Contest contest) {
        if (canSuperviseProblems(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canSuperviseProblems(String userJid, Contest contest) {
        return isSupervisorWithPermissionOrAbove(userJid, contest, PROBLEM);
    }

    public Optional<String> canSubmitProblem(
            String userJid,
            Contest contest,
            ContestContestantProblem contestantProblem) {

        if (!contestRoleDao.isContestant(userJid, contest.getJid())
                && !isSupervisorWithPermissionOrAbove(userJid, contest, PROBLEM)) {
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

    public boolean canViewDefaultScoreboard(String userJid, Contest contest) {
        if (canSuperviseScoreboard(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canSuperviseScoreboard(String userJid, Contest contest) {
        return isSupervisorWithPermissionOrAbove(userJid, contest, SCOREBOARD);
    }

    public boolean canViewSubmission(String userJid, Contest contest, String submissionUserJid) {
        return userJid.equals(submissionUserJid) || isSupervisorWithPermissionOrAbove(userJid, contest, SUBMISSION);
    }

    public boolean canViewOwnSubmissions(String userJid, Contest contest) {
        if (canSuperviseSubmissions(userJid, contest)) {
            return true;
        }
        return contestRoleDao.isContestantOrAbove(userJid, contest.getJid())
                && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canSuperviseSubmissions(String userJid, Contest contest) {
        return isSupervisorWithPermissionOrAbove(userJid, contest, SUBMISSION);
    }

    public boolean canAddContestants(String userJid, Contest contest) {
        return adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid());
    }

    private boolean isSupervisorWithPermissionOrAbove(String userJid, Contest contest, SupervisorPermissionType type) {
        if (adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid())) {
            return true;
        }
        Optional<ContestSupervisor> supervisor = supervisorStore.findSupervisor(contest.getJid(), userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(type);
    }
}
