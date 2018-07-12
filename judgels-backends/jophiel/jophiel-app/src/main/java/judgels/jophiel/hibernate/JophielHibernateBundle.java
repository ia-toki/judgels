package judgels.jophiel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jophiel.JophielApplicationConfiguration;
import judgels.jophiel.legacy.session.LegacySessionModel;
import judgels.jophiel.persistence.AdminRoleModel;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserProfileModel;
import judgels.jophiel.persistence.UserRegistrationEmailModel;
import judgels.jophiel.persistence.UserResetPasswordModel;

public class JophielHibernateBundle extends HibernateBundle<JophielApplicationConfiguration> {
    public JophielHibernateBundle() {
        super(
                AdminRoleModel.class,
                LegacySessionModel.class,
                SessionModel.class,
                UserModel.class,
                UserProfileModel.class,
                UserRegistrationEmailModel.class,
                UserResetPasswordModel.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JophielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
