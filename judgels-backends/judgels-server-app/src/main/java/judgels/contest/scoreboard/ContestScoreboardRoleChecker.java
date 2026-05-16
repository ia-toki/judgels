package judgels.contest.scoreboard;

import static judgels.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.api.contest.supervisor.SupervisorManagementPermission.SCOREBOARD;

import jakarta.inject.Inject;
import judgels.api.contest.Contest;
import judgels.api.contest.ContestStyle;
import judgels.contest.ContestRoleChecker;
import judgels.contest.ContestTimer;
import judgels.contest.problem.ContestProblemStore;
import judgels.contest.supervisor.ContestSupervisorStore;
import judgels.persistence.ContestRoleDao;

public class ContestScoreboardRoleChecker {
    private final ContestRoleChecker contestRoleChecker;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestProblemStore problemStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestScoreboardRoleChecker(
            ContestRoleChecker contestRoleChecker,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestScoreboardStore scoreboardStore,
            ContestProblemStore problemStore,
            ContestSupervisorStore supervisorStore) {

        this.contestRoleChecker = contestRoleChecker;
        this.contestTimer = contestTimer;
        this.contestRoleDao = contestRoleDao;
        this.scoreboardStore = scoreboardStore;
        this.problemStore = problemStore;
        this.supervisorStore = supervisorStore;
    }

    public boolean canViewDefault(String userJid, Contest contest) {
        if (canSupervise(userJid, contest)) {
            return true;
        }

        if (contest.getStyle() == ContestStyle.BUNDLE) {
            return false;
        }

        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canViewOfficialAndFrozen(String userJid, Contest contest) {
        return canSupervise(userJid, contest) && scoreboardStore.getScoreboard(contest.getJid(), FROZEN).isPresent();
    }

    public boolean canViewClosedProblems(String userJid, Contest contest) {
        return canSupervise(userJid, contest) && problemStore.hasClosedProblems(contest.getJid());
    }

    public boolean canSupervise(String userJid, Contest contest) {
        return contestRoleChecker.canSupervise(userJid, contest);
    }

    public boolean canManage(String userJid, Contest contest) {
        return contestRoleChecker.canManage(userJid, contest)
                || supervisorStore.isSupervisorWithManagementPermission(contest.getJid(), userJid, SCOREBOARD);

    }
}
