package judgels.jophiel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jophiel.JophielApplicationConfiguration;
import judgels.jophiel.legacy.session.LegacySessionModel;
import judgels.jophiel.role.AdminRoleModel;
import judgels.jophiel.session.SessionModel;
import judgels.jophiel.user.UserModel;
import judgels.jophiel.user.password.UserResetPasswordModel;
import judgels.jophiel.user.profile.UserProfileModel;
import judgels.jophiel.user.registration.UserRegistrationEmailModel;

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
