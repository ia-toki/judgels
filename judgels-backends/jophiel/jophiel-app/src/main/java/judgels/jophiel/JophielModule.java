package judgels.jophiel;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;
import judgels.service.actor.ActorChecker;

@Module
public class JophielModule {
    private JophielModule() {}

    @Provides
    @Singleton
    static ActorChecker actorChecker(SessionStore sessionStore) {
        return new ActorChecker(authHeader ->
                sessionStore.getSessionByToken(authHeader.getBearerToken()).map(Session::getUserJid));
    }
}
