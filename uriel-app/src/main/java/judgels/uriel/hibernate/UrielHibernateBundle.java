package judgels.uriel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.uriel.UrielApplicationConfiguration;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestSupervisorModel;

public class UrielHibernateBundle extends HibernateBundle<UrielApplicationConfiguration> {
    public UrielHibernateBundle() {
        super(
                AdminRoleModel.class,
                ContestModel.class,
                ContestStyleModel.class,
                ContestAnnouncementModel.class,
                ContestScoreboardModel.class,
                ContestContestantModel.class,
                ContestSupervisorModel.class,
                ContestManagerModel.class,
                ContestModuleModel.class,
                ContestProblemModel.class
        );
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(UrielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
