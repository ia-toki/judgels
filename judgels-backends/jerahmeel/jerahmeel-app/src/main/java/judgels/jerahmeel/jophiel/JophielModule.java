package judgels.jerahmeel.jophiel;

import com.palantir.conjure.java.api.config.service.UserAgent;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.jophiel.api.user.rating.UserRatingService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class JophielModule {
    private final JophielClientConfiguration config;

    public JophielModule(JophielClientConfiguration config) {
        this.config = config;
    }

    @Provides
    JophielClientConfiguration jophielConfig() {
        return config;
    }

    @Provides
    @Singleton
    UserService userService(UserAgent agent) {
        return JaxRsClients.create(UserService.class, config.getBaseUrl(), agent);
    }

    @Provides
    @Singleton
    UserSearchService userSearchService(UserAgent agent) {
        return JaxRsClients.create(UserSearchService.class, config.getBaseUrl(), agent);
    }

    @Provides
    @Singleton
    UserRatingService userRatingService(UserAgent agent) {
        return JaxRsClients.create(UserRatingService.class, config.getBaseUrl(), agent);
    }

    @Provides
    @Singleton
    ProfileService profileService(UserAgent agent) {
        return JaxRsClients.create(ProfileService.class, config.getBaseUrl(), agent);
    }

    @Provides
    @Singleton
    MyUserService myService(UserAgent agent) {
        return JaxRsClients.create(MyUserService.class, config.getBaseUrl(), agent);
    }
}
