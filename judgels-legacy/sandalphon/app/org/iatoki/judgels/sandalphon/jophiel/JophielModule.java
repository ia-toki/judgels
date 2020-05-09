package org.iatoki.judgels.sandalphon.jophiel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.palantir.conjure.java.api.config.service.UserAgent;
import judgels.jophiel.api.JophielClientConfiguration;
import judgels.jophiel.api.client.user.ClientUserService;
import judgels.jophiel.api.play.PlaySessionService;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.service.jaxrs.JaxRsClients;

public final class JophielModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    JophielClientConfiguration config(SandalphonConfiguration config) {
        return config.getJophielConfig();
    }

    @Provides
    PlaySessionService sessionService(JophielClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(PlaySessionService.class, config.getBaseUrl(), userAgent);
    }

    @Provides
    ClientUserService clientService(JophielClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(ClientUserService.class, config.getBaseUrl(), userAgent);
    }

    @Provides
    MyUserService myUserService(JophielClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(MyUserService.class, config.getBaseUrl(), userAgent);
    }

    @Provides
    UserSearchService userSearchService(JophielClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(UserSearchService.class, config.getBaseUrl(), userAgent);
    }

    @Provides
    ProfileService profileService(JophielClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(ProfileService.class, config.getBaseUrl(), userAgent);
    }
}
