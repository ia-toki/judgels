package judgels.uriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.announcement.ContestAnnouncementStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.manager.ContestManagerStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;
import judgels.uriel.hibernate.UrielHibernateDaoModule;
import judgels.uriel.role.AdminRoleStore;
import judgels.uriel.role.RoleChecker;

@Component(modules = {
        UrielHibernateDaoModule.class,
        UrielIntegrationTestHibernateModule.class,
        UrielIntegrationTestPersistenceModule.class})
@Singleton
public interface UrielIntegrationTestComponent {
    AdminRoleStore adminRoleStore();
    ContestStore contestStore();
    ContestModuleStore contestModuleStore();
    ContestAnnouncementStore contestAnnouncementStore();
    ContestContestantStore contestContestantStore();
    ContestSupervisorStore contestSupervisorStore();
    ContestManagerStore contestManagerStore();
    ContestScoreboardStore contestScoreboardStore();

    RoleChecker roleChecker();
}
