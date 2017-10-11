package judgels.jophiel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jophiel.JophielApplicationConfiguration;
import judgels.jophiel.hibernate.user.UserModel;

public class JophielHibernateBundle extends HibernateBundle<JophielApplicationConfiguration> {
    public JophielHibernateBundle() {
        super(UserModel.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JophielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
