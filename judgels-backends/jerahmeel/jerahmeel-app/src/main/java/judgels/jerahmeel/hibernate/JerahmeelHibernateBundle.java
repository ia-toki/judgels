package judgels.jerahmeel.hibernate;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import judgels.jerahmeel.JerahmeelApplicationConfiguration;
import judgels.jerahmeel.persistence.AdminRoleModel;
import judgels.jerahmeel.persistence.CourseModel;

public class JerahmeelHibernateBundle extends HibernateBundle<JerahmeelApplicationConfiguration> {
    public JerahmeelHibernateBundle() {
        super(
                AdminRoleModel.class,
                CourseModel.class
        );
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(JerahmeelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
