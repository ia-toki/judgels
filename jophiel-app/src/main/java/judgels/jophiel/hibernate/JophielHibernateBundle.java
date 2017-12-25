package judgels.jophiel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jophiel.JophielApplicationConfiguration;
import judgels.jophiel.session.SessionModel;
import judgels.jophiel.user.UserModel;
import judgels.jophiel.user.password.UserForgotPasswordModel;
import judgels.jophiel.user.profile.UserProfileModel;
import judgels.jophiel.user.registration.UserRegistrationEmailModel;

public class JophielHibernateBundle extends HibernateBundle<JophielApplicationConfiguration> {
    public JophielHibernateBundle() {
        super(
                SessionModel.class,
                UserModel.class,
                UserProfileModel.class,
                UserRegistrationEmailModel.class,
                UserForgotPasswordModel.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JophielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
