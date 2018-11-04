package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsPersistenceModule;
import judgels.service.hibernate.JudgelsHibernateModule;
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
import judgels.uriel.contest.scoreboard.ContestScoreboardRoleChecker;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.submission.ContestSubmissionRoleChecker;
import judgels.uriel.contest.submission.ContestSubmissionStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.role.AdminRoleStore;

@Component(modules = {
        JudgelsModule.class,
        JudgelsHibernateModule.class,
        JudgelsPersistenceModule.class,
        UrielModule.class,
        UrielHibernateDaoModule.class})
@Singleton
public interface UrielIntegrationTestComponent {
    AdminRoleStore adminRoleStore();
    ContestStore contestStore();
    ContestModuleStore contestModuleStore();
    ContestAnnouncementStore contestAnnouncementStore();
    ContestClarificationStore contestClarificationStore();
    ContestContestantStore contestContestantStore();
    ContestManagerStore contestManagerStore();
    ContestScoreboardStore contestScoreboardStore();
    ContestSubmissionStore contestSubmissionStore();
    ContestSupervisorStore contestSupervisorStore();

    ContestRoleChecker contestRoleChecker();
    ContestAnnouncementRoleChecker contestAnnouncementRoleChecker();
    ContestClarificationRoleChecker contestClarificationRoleChecker();
    ContestContestantRoleChecker contestContestantRoleChecker();
    ContestFileRoleChecker contestFileRoleChecker();
    ContestProblemRoleChecker contestProblemRoleChecker();
    ContestScoreboardRoleChecker contestScoreboardRoleChecker();
    ContestSubmissionRoleChecker contestSubmissionRoleChecker();
}
