package judgels.michael;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.UserInfoModel;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.jophiel.persistence.UserRegistrationEmailModel;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.jophiel.persistence.UserRoleModel;
import judgels.jophiel.play.PlaySessionModel;
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

public class MichaelHibernateBundle extends HibernateBundle<MichaelApplicationConfiguration> {
    public MichaelHibernateBundle() {
        super(
                PlaySessionModel.class,
                SessionModel.class,
                UserModel.class,
                UserInfoModel.class,
                UserRatingModel.class,
                UserRatingEventModel.class,
                UserRegistrationEmailModel.class,
                UserResetPasswordModel.class,
                UserRoleModel.class,
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
                ContestLogModel.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(MichaelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
