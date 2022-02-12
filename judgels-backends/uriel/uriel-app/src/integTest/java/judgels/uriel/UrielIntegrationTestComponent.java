package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
import judgels.uriel.contest.ContestGroupStore;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantRoleChecker;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.editorial.ContestEditorialRoleChecker;
import judgels.uriel.contest.file.ContestFileRoleChecker;
import judgels.uriel.contest.group.ContestGroupContestStore;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardRoleChecker;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.jophiel.JophielModule;
import judgels.uriel.role.RoleChecker;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        JophielModule.class,
        UrielModule.class,
        UrielHibernateDaoModule.class})
@Singleton
public interface UrielIntegrationTestComponent {
    SubmissionStore submissionStore();

    RoleChecker roleChecker();
    ContestStore contestStore();
    ContestModuleStore contestModuleStore();
    ContestContestantStore contestContestantStore();
    ContestGroupStore contestGroupStore();
    ContestGroupContestStore contestGroupContestStore();
    ContestManagerStore contestManagerStore();
    ContestProblemStore contestProblemStore();
    ContestScoreboardStore contestScoreboardStore();
    ContestSupervisorStore contestSupervisorStore();

    ContestRoleChecker contestRoleChecker();
    ContestContestantRoleChecker contestContestantRoleChecker();
    ContestFileRoleChecker contestFileRoleChecker();
    ContestProblemRoleChecker contestProblemRoleChecker();
    ContestEditorialRoleChecker contestEditorialRoleChecker();
    ContestScoreboardRoleChecker contestScoreboardRoleChecker();
    ContestSubmissionRoleChecker contestSubmissionRoleChecker();
}
