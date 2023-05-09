package judgels.service.hibernate;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.hibernate.SessionFactory;

@Module
public class JudgelsHibernateModule {
    private final SessionFactory sessionFactory;
    private final UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;

    public JudgelsHibernateModule(HibernateBundle<?> hibernateBundle) {
        this.sessionFactory = hibernateBundle.getSessionFactory();
        this.unitOfWorkAwareProxyFactory = new UnitOfWorkAwareProxyFactory(hibernateBundle);
    }

    public JudgelsHibernateModule(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.unitOfWorkAwareProxyFactory = null;
    }

    @Provides
    SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Provides
    UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory() {
        return unitOfWorkAwareProxyFactory;
    }
}
