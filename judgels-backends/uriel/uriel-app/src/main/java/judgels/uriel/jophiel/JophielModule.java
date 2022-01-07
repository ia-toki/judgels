package judgels.uriel.jophiel;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.jophiel.api.client.user.ClientUserService;
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
    UserService userService() {
        return JaxRsClients.create(UserService.class, config.getBaseUrl());
    }

    @Provides
    @Singleton
    UserSearchService userSearchService() {
        return JaxRsClients.create(UserSearchService.class, config.getBaseUrl());
    }

    @Provides
    @Singleton
    UserRatingService userRatingService() {
        return JaxRsClients.create(UserRatingService.class, config.getBaseUrl());
    }

    @Provides
    @Singleton
    ProfileService profileService() {
        return JaxRsClients.create(ProfileService.class, config.getBaseUrl());
    }

    @Provides
    @Singleton
    MyUserService myService() {
        return JaxRsClients.create(MyUserService.class, config.getBaseUrl());
    }

    @Provides
    @Singleton
    ClientUserService clientUserService() {
        return JaxRsClients.create(ClientUserService.class, config.getBaseUrl());
    }
}
