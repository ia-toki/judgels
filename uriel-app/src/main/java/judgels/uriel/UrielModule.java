package judgels.uriel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.remoting.api.errors.RemoteException;
import com.palantir.remoting3.clients.UserAgent;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.jackson.Jackson;
import java.time.Clock;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.jophiel.api.user.MyService;
import judgels.persistence.ActorProvider;
import judgels.service.actor.ActorChecker;
import judgels.service.actor.PerRequestActorProvider;

@Module
public class UrielModule {
    private UrielModule() {}

    @Provides
    @Singleton
    static UserAgent userAgent() {
        String version = UrielModule.class.getPackage().getImplementationVersion();
        if (version == null) {
            version = UserAgent.Agent.DEFAULT_VERSION;
        }

        return UserAgent.of(UserAgent.Agent.of("uriel", version));
    }

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
    static ActorChecker actorChecker(MyService myService) {
        return new ActorChecker(authHeader -> {
            try {
                return Optional.of(myService.getMyself(authHeader).getJid());
            } catch (RemoteException e) {
                return Optional.empty();
            }
        });
    }
}
