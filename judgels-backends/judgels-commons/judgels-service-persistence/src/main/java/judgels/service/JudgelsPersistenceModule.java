package judgels.service;

import dagger.Module;
import dagger.Provides;
import java.time.Clock;
import judgels.persistence.ActorProvider;
import judgels.service.actor.JudgelsActorProvider;

@Module
public class JudgelsPersistenceModule {
    private final Clock clock;
    private final ActorProvider actorProvider;

    public JudgelsPersistenceModule() {
        this(Clock.systemUTC(), new JudgelsActorProvider());
    }

    public JudgelsPersistenceModule(Clock clock, ActorProvider actorProvider) {
        this.clock = clock;
        this.actorProvider = actorProvider;
    }

    @Provides
    Clock clock() {
        return clock;
    }

    @Provides
    ActorProvider actorProvider() {
        return actorProvider;
    }
}
