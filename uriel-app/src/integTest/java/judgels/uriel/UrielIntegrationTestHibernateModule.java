package judgels.uriel;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.hibernate.SessionFactory;

@Module
public class UrielIntegrationTestHibernateModule {
    private final SessionFactory sessionFactory;

    public UrielIntegrationTestHibernateModule(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Provides
    @Singleton
    SessionFactory sessionFactory() {
        return sessionFactory;
    }
}
