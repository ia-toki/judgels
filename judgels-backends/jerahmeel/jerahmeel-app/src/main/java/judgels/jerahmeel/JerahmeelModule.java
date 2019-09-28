package judgels.jerahmeel;

import com.palantir.conjure.java.api.config.service.UserAgent;
import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Singleton;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.service.JudgelsVersion;
import judgels.service.actor.ActorChecker;
import judgels.service.actor.CachingActorExtractor;

@Module
public class JerahmeelModule {
    private final JerahmeelConfiguration config;

    public JerahmeelModule(JerahmeelConfiguration config) {
        this.config = config;
    }

    @Provides
    @JerahmeelBaseDataDir
    Path jerahmeelBaseDataDir() {
        return Paths.get(config.getBaseDataDir());
    }

    @Provides
    @Singleton
    static UserAgent userAgent() {
        return UserAgent.of(UserAgent.Agent.of("jerahmeel", JudgelsVersion.INSTANCE));
    }

    @Provides
    @Singleton
    static ActorChecker actorChecker(MyUserService myUserService) {
        return new ActorChecker(new CachingActorExtractor(myUserService));
    }
}
