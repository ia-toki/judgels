package judgels.jophiel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jophiel.JophielApplicationConfiguration;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.UserInfoModel;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserRatingEventModel;
import judgels.jophiel.persistence.UserRatingModel;
import judgels.jophiel.persistence.UserRegistrationEmailModel;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.jophiel.persistence.UserRoleModel;
import judgels.jophiel.play.PlaySessionModel;

public class JophielHibernateBundle extends HibernateBundle<JophielApplicationConfiguration> {
    public JophielHibernateBundle() {
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
    public PooledDataSourceFactory getDataSourceFactory(JophielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
