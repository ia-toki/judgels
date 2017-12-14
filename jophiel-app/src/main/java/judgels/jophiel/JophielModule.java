package judgels.jophiel;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.jackson.Jackson;
import java.time.Clock;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;
import judgels.persistence.ActorProvider;
import judgels.service.actor.ActorChecker;
import judgels.service.actor.PerRequestActorProvider;

@Module
public class JophielModule {
    private JophielModule() {}

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return Jackson.newObjectMapper();
    }

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

    @Provides
    @Singleton
    static ActorChecker actorChecker(SessionStore sessionStore) {
        return new ActorChecker(authHeader ->
                sessionStore.findSessionByToken(authHeader.getBearerToken()).map(Session::getUserJid));
    }
}
