package judgels.jophiel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.remoting3.ext.jackson.ObjectMappers;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.jersey.JudgelsObjectMappers;

@Module
public class JophielModule {
    private JophielModule() {}

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return JudgelsObjectMappers.configure(ObjectMappers.newClientObjectMapper());
    }

    @Provides
    @Singleton
    static ActorChecker actorChecker(SessionStore sessionStore) {
        return new ActorChecker(authHeader ->
                sessionStore.findSessionByToken(authHeader.getBearerToken()).map(Session::getUserJid));
    }
}
