package judgels.uriel;

import com.palantir.remoting3.clients.UserAgent;
import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Singleton;
import judgels.jophiel.api.user.MyService;
import judgels.service.JudgelsVersion;
import judgels.service.actor.ActorChecker;
import judgels.service.actor.CachingActorExtractor;

@Module
public class UrielModule {
    private final UrielConfiguration config;

    public UrielModule(UrielConfiguration config) {
        this.config = config;
    }

    @Provides
    @UrielBaseDataDir
    Path urielBaseDataDir() {
        return Paths.get(config.getBaseDataDir());
    }

    @Provides
    @Singleton
    static UserAgent userAgent() {
        return UserAgent.of(UserAgent.Agent.of("uriel", JudgelsVersion.INSTANCE));
    }

    @Provides
    @Singleton
    static ActorChecker actorChecker(MyService myService) {
        return new ActorChecker(new CachingActorExtractor(myService));
    }
}
