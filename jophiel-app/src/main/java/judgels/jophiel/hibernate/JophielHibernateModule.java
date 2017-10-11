package judgels.jophiel.hibernate;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.jophiel.hibernate.user.UserHibernateStore;
import judgels.jophiel.user.UserStore;
import org.hibernate.SessionFactory;

@Module
public class JophielHibernateModule {
    private final SessionFactory sessionFactory;

    public JophielHibernateModule(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Provides
    @Singleton
    public UserStore userDao() {
        return new UserHibernateStore(sessionFactory);
    }
}
