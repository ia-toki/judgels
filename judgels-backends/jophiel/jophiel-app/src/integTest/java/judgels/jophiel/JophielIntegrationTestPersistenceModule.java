package judgels.jophiel;

import dagger.Module;
import dagger.Provides;
import java.time.Clock;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;

@Module
public class JophielIntegrationTestPersistenceModule {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public JophielIntegrationTestPersistenceModule() {
        this(new TestClock(), new TestActorProvider());
    }

    public JophielIntegrationTestPersistenceModule(Clock clock) {
        this(clock, new TestActorProvider());
    }

    public JophielIntegrationTestPersistenceModule(ActorProvider actorProvider) {
        this(new TestClock(), actorProvider);
    }

    public JophielIntegrationTestPersistenceModule(Clock clock, ActorProvider actorProvider) {
        this.clock = clock;
        this.actorProvider = actorProvider;
    }

    @Provides
    @Singleton
    Clock clock() {
        return clock;
    }

    @Provides
    @Singleton
    ActorProvider actorProvider() {
        return actorProvider;
    }
}
