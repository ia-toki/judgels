package judgels.uriel.hibernate;

import dagger.Module;
import dagger.Provides;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestGradingDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestStyleDao;
import judgels.uriel.persistence.ContestSubmissionDao;
import judgels.uriel.persistence.ContestSupervisorDao;

@Module
public class UrielHibernateDaoModule {
    private UrielHibernateDaoModule() {}

    @Provides
    static AdminRoleDao adminRoleDao(AdminRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestDao contestDao(ContestHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestStyleDao contestStyleDao(ContestStyleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestAnnouncementDao contestAnnouncementDao(ContestAnnouncementHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestScoreboardDao contestScoreboardDao(ContestScoreboardHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestContestantDao contestContestantDao(ContestContestantHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestSupervisorDao contestSupervisorDao(ContestSupervisorHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestManagerDao contestManagerDao(ContestManagerHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestRoleDao contestRoleDao(ContestRoleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestModuleDao contestModuleDao(ContestModuleHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestProblemDao contestProblemDao(ContestProblemHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestSubmissionDao contestSubmissionDao(ContestSubmissionHibernateDao dao) {
        return dao;
    }

    @Provides
    static ContestGradingDao contestGradingDao(ContestGradingHibernateDao dao) {
        return dao;
    }
}
