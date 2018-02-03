package judgels.uriel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.uriel.UrielApplicationConfiguration;
import judgels.uriel.contest.ContestModel;

public class UrielHibernateBundle extends HibernateBundle<UrielApplicationConfiguration> {
    public UrielHibernateBundle() {
        super(ContestModel.class);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(UrielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
