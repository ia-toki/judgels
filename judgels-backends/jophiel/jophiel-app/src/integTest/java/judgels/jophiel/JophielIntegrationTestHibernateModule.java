package judgels.jophiel;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.hibernate.SessionFactory;

@Module
public class JophielIntegrationTestHibernateModule {
    private final SessionFactory sessionFactory;

    public JophielIntegrationTestHibernateModule(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Provides
    @Singleton
    SessionFactory sessionFactory() {
        return sessionFactory;
    }
}
