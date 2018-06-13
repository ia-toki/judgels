package judgels.uriel;

import static judgels.uriel.VersionResource.VERSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.remoting.api.errors.RemoteException;
import com.palantir.remoting3.clients.UserAgent;
import com.palantir.remoting3.ext.jackson.ObjectMappers;
import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import javax.ws.rs.NotAuthorizedException;
import judgels.jophiel.api.user.MyService;
import judgels.service.actor.ActorChecker;
import judgels.service.jersey.JudgelsObjectMappers;

@Module
public class UrielModule {
    private UrielModule() {}

    @Provides
    @Singleton
    static UserAgent userAgent() {
        String version = VERSION == null ? UserAgent.Agent.DEFAULT_VERSION : VERSION;
        return UserAgent.of(UserAgent.Agent.of("uriel", version));
    }

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return JudgelsObjectMappers.configure(ObjectMappers.newClientObjectMapper());
    }

    @Provides
    @Singleton
    static ActorChecker actorChecker(MyService myService) {
        return new ActorChecker(authHeader -> {
            try {
                return Optional.of(myService.getMyself(authHeader).getJid());
            } catch (RemoteException e) {
                if (e.getStatus() == 401) {
                    throw new NotAuthorizedException(e);
                }
            }
            return Optional.empty();
        });
    }
}
