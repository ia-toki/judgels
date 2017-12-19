package judgels.jophiel.hibernate;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.time.Clock;
import javax.inject.Singleton;
import judgels.jophiel.session.SessionDao;
import judgels.jophiel.user.UserDao;
import judgels.jophiel.user.email.UserRegistrationEmailDao;
import judgels.jophiel.user.profile.UserProfileDao;
import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;

@Module
public class JophielHibernateModule {
    private final HibernateBundle<?> hibernateBundle;
    private final SessionFactory sessionFactory;

    public JophielHibernateModule(HibernateBundle<?> hibernateBundle) {
        this.hibernateBundle = hibernateBundle;
        this.sessionFactory = hibernateBundle.getSessionFactory();
    }

    @Provides
    @Singleton
    SessionFactory sessionFactory() {
        return sessionFactory;
    }

    @Provides
    @Singleton
    UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory() {
        return new UnitOfWorkAwareProxyFactory(hibernateBundle);
    }

    @Provides
    @Singleton
    SessionDao sessionDao(Clock clock, ActorProvider actorProvider) {
        return new SessionHibernateDao(sessionFactory, clock, actorProvider);
    }

    @Provides
    @Singleton
    UserDao userDao(Clock clock, ActorProvider actorProvider) {
        return new UserHibernateDao(sessionFactory, clock, actorProvider);
    }

    @Provides
    @Singleton
    UserProfileDao userProfileDao(Clock clock, ActorProvider actorProvider) {
        return new UserProfileHibernateDao(sessionFactory, clock, actorProvider);
    }

    @Provides
    @Singleton
    UserRegistrationEmailDao userRegistrationEmailDao(Clock clock, ActorProvider actorProvider) {
        return new UserRegistrationEmailHibernateDao(sessionFactory, clock, actorProvider);
    }
}
