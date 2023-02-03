package judgels.uriel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.uriel.UrielApplicationConfiguration;
import judgels.uriel.persistence.ContestAnnouncementModel;
import judgels.uriel.persistence.ContestBundleItemSubmissionModel;
import judgels.uriel.persistence.ContestClarificationModel;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestLogModel;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import judgels.uriel.persistence.ContestProblemModel;
import judgels.uriel.persistence.ContestProgrammingGradingModel;
import judgels.uriel.persistence.ContestProgrammingSubmissionModel;
import judgels.uriel.persistence.ContestScoreboardModel;
import judgels.uriel.persistence.ContestStyleModel;
import judgels.uriel.persistence.ContestSupervisorModel;

public class UrielHibernateBundle extends HibernateBundle<UrielApplicationConfiguration> {
    public UrielHibernateBundle() {
        super(
                ContestModel.class,
                ContestAnnouncementModel.class,
                ContestBundleItemSubmissionModel.class,
                ContestClarificationModel.class,
                ContestContestantModel.class,
                ContestManagerModel.class,
                ContestModuleModel.class,
                ContestProblemModel.class,
                ContestProgrammingGradingModel.class,
                ContestProgrammingSubmissionModel.class,
                ContestScoreboardModel.class,
                ContestStyleModel.class,
                ContestSupervisorModel.class,
                ContestLogModel.class
        );
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(UrielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
