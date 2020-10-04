package judgels.jophiel;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionConfiguration;
import judgels.jophiel.session.SessionStore;
import judgels.service.actor.ActorChecker;

@Module
public class JophielModule {
    private final JophielConfiguration config;

    public JophielModule(JophielConfiguration config) {
        this.config = config;
    }

    @Provides
    SessionConfiguration sessionConfig() {
        return this.config.getSessionConfig();
    }

    @Provides
    @Singleton
    static ActorChecker actorChecker(SessionStore sessionStore) {
        return new ActorChecker(authHeader ->
                sessionStore.getSessionByToken(authHeader.getBearerToken()).map(Session::getUserJid));
    }
}
