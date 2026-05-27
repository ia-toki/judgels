package judgels.persistence.hibernate;

import jakarta.inject.Inject;
import java.time.Clock;
import judgels.persistence.ActorProvider;
import org.hibernate.SessionFactory;

public class HibernateDaoData {
    private final SessionFactory sessionFactory;
    private final Clock clock;
    private final ActorProvider actorProvider;

    @Inject
    public HibernateDaoData(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        this.sessionFactory = sessionFactory;
        this.clock = clock;
        this.actorProvider = actorProvider;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Clock getClock() {
        return clock;
    }

    public ActorProvider getActorProvider() {
        return actorProvider;
    }
}
