package judgels.persistence;

import jakarta.inject.Inject;
import java.time.Clock;
import org.hibernate.SessionFactory;

public class DaoData {
    private final SessionFactory sessionFactory;
    private final Clock clock;
    private final ActorProvider actorProvider;

    @Inject
    public DaoData(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
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
