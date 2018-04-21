package judgels.jophiel;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.jackson.Jackson;
import javax.inject.Singleton;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;
import judgels.service.actor.ActorChecker;

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
    static ActorChecker actorChecker(SessionStore sessionStore) {
        return new ActorChecker(authHeader ->
                sessionStore.findSessionByToken(authHeader.getBearerToken()).map(Session::getUserJid));
    }
}
