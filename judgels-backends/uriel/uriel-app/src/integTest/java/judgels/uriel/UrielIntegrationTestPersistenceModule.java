package judgels.uriel;

import dagger.Module;
import dagger.Provides;
import java.time.Clock;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.persistence.TestActorProvider;
import judgels.persistence.TestClock;

@Module
public class UrielIntegrationTestPersistenceModule {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public UrielIntegrationTestPersistenceModule() {
        this(new TestClock(), new TestActorProvider());
    }

    public UrielIntegrationTestPersistenceModule(Clock clock) {
        this(clock, new TestActorProvider());
    }

    public UrielIntegrationTestPersistenceModule(ActorProvider actorProvider) {
        this(new TestClock(), actorProvider);
    }

    public UrielIntegrationTestPersistenceModule(Clock clock, ActorProvider actorProvider) {
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
