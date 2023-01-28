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
                UserRoleModel.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(MichaelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
