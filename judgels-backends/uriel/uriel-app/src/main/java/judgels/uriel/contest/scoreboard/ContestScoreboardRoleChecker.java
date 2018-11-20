package judgels.uriel.contest.scoreboard;

import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.supervisor.SupervisorPermissionType.SCOREBOARD;

import java.util.Optional;
import javax.inject.Inject;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestRoleDao;

public class ContestScoreboardRoleChecker {
    private final AdminRoleDao adminRoleDao;
    private final ContestRoleDao contestRoleDao;
    private final ContestTimer contestTimer;
    private final ContestScoreboardStore scoreboardStore;
    private final ContestProblemStore problemStore;
    private final ContestSupervisorStore supervisorStore;

    @Inject
    public ContestScoreboardRoleChecker(
            AdminRoleDao adminRoleDao,
            ContestRoleDao contestRoleDao,
            ContestTimer contestTimer,
            ContestScoreboardStore scoreboardStore,
            ContestProblemStore problemStore,
            ContestSupervisorStore supervisorStore) {

        this.adminRoleDao = adminRoleDao;
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
        return contestRoleDao.isViewerOrAbove(userJid, contest.getJid()) && contestTimer.hasStarted(contest, userJid);
    }

    public boolean canViewOfficialAndFrozen(String userJid, Contest contest) {
        return canSupervise(userJid, contest) && scoreboardStore.getScoreboard(contest.getJid(), FROZEN).isPresent();
    }

    public boolean canViewClosedProblems(String userJid, Contest contest) {
        return canSupervise(userJid, contest) && problemStore.hasClosedProblems(contest.getJid());
    }

    public boolean canSupervise(String userJid, Contest contest) {
        if (adminRoleDao.isAdmin(userJid) || contestRoleDao.isManager(userJid, contest.getJid())) {
            return true;
        }
        Optional<ContestSupervisor> supervisor = supervisorStore.getSupervisor(contest.getJid(), userJid);
        return supervisor.isPresent() && supervisor.get().getPermission().allows(SCOREBOARD);
    }
}
