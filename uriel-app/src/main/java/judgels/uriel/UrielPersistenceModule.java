package judgels.uriel;

import dagger.Module;
import dagger.Provides;
import java.time.Clock;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;
import judgels.service.actor.PerRequestActorProvider;

@Module
public class UrielPersistenceModule {
    private UrielPersistenceModule() {}

    @Provides
    @Singleton
    static Clock clock() {
        return Clock.systemUTC();
    }

    @Provides
    @Singleton
    static ActorProvider actorProvider() {
        return new ActorProvider() {
            @Override
            public Optional<String> getJid() {
                return PerRequestActorProvider.getJid();
            }

            @Override
            public Optional<String> getIpAddress() {
                return PerRequestActorProvider.getIpAddress();
            }
        };
    }
}
