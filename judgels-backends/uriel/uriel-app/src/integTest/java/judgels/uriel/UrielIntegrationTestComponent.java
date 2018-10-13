package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.announcement.ContestAnnouncementRoleChecker;
import judgels.uriel.contest.announcement.ContestAnnouncementStore;
import judgels.uriel.contest.clarification.ContestClarificationRoleChecker;
import judgels.uriel.contest.clarification.ContestClarificationStore;
import judgels.uriel.contest.contestant.ContestContestantRoleChecker;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.file.ContestFileRoleChecker;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemRoleChecker;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardRoleChecker;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.style.ContestStyleStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.role.AdminRoleStore;

@Component(modules = {
        UrielHibernateDaoModule.class,
        UrielIntegrationTestModule.class,
        UrielIntegrationTestHibernateModule.class,
        UrielIntegrationTestPersistenceModule.class})
@Singleton
public interface UrielIntegrationTestComponent {
    AdminRoleStore adminRoleStore();
    ContestStore contestStore();
    ContestStyleStore contestStyleStore();
    ContestModuleStore contestModuleStore();
    ContestAnnouncementStore contestAnnouncementStore();
    ContestClarificationStore contestClarificationStore();
    ContestContestantStore contestContestantStore();
    ContestSupervisorStore contestSupervisorStore();
    ContestManagerStore contestManagerStore();
    ContestScoreboardStore contestScoreboardStore();
    ContestProblemStore contestProblemStore();

    ContestRoleChecker contestRoleChecker();
    ContestAnnouncementRoleChecker contestAnnouncementRoleChecker();
    ContestClarificationRoleChecker contestClarificationRoleChecker();
    ContestContestantRoleChecker contestContestantRoleChecker();
    ContestFileRoleChecker contestFileRoleChecker();
    ContestProblemRoleChecker contestProblemRoleChecker();
    ContestScoreboardRoleChecker contestScoreboardRoleChecker();
    ContestSubmissionRoleChecker contestSubmissionRoleChecker();
}
