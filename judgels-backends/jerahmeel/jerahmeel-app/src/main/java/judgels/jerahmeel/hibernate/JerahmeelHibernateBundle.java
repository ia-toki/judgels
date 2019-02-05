package judgels.jerahmeel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jerahmeel.JerahmeelApplicationConfiguration;
import judgels.jerahmeel.persistence.AdminRoleModel;

public class JerahmeelHibernateBundle extends HibernateBundle<JerahmeelApplicationConfiguration> {
    public JerahmeelHibernateBundle() {
        super(
                AdminRoleModel.class
                // ContestModel.class,
                // ContestAnnouncementModel.class,
                // ContestClarificationModel.class,
                // ContestContestantModel.class,
                // ContestGradingModel.class,
                // ContestManagerModel.class,
                // ContestModuleModel.class,
                // ContestProblemModel.class,
                // ContestScoreboardModel.class,
                // ContestStyleModel.class,
                // ContestSubmissionModel.class,
                // ContestSupervisorModel.class
        );
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JerahmeelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
