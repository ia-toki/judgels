package judgels.jophiel;

import dagger.Module;
import dagger.Provides;
import java.time.Clock;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.persistence.ActorProvider;

@Module
public class JophielModule {
    private JophielModule() {}

    @Provides
    @Singleton
    static Clock clock() {
        return Clock.systemUTC();
    }

    @Provides
    @Singleton
    static ActorProvider ipProvider() {
        return new ActorProvider() {
            @Override
            public Optional<String> getJid() {
                return Optional.empty();
            }

            @Override
            public String getIpAddress() {
                return "jid";
            }
        };
    }
}
