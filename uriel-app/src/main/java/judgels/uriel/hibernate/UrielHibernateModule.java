package judgels.uriel.hibernate;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import org.hibernate.SessionFactory;

@Module
public class UrielHibernateModule {
    private final HibernateBundle<?> hibernateBundle;

    public UrielHibernateModule(HibernateBundle<?> hibernateBundle) {
        this.hibernateBundle = hibernateBundle;
    }

    @Provides
    @Singleton
    SessionFactory sessionFactory() {
        return hibernateBundle.getSessionFactory();
    }

    @Provides
    @Singleton
    UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory() {
        return new UnitOfWorkAwareProxyFactory(hibernateBundle);
    }
}
