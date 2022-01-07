package org.iatoki.judgels.sandalphon.jophiel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
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
        return JaxRsClients.create(PlaySessionService.class, config.getBaseUrl());
    }

    @Provides
    ClientUserService clientService(JophielClientConfiguration config) {
        return JaxRsClients.create(ClientUserService.class, config.getBaseUrl());
    }

    @Provides
    MyUserService myUserService(JophielClientConfiguration config) {
        return JaxRsClients.create(MyUserService.class, config.getBaseUrl());
    }

    @Provides
    UserSearchService userSearchService(JophielClientConfiguration config) {
        return JaxRsClients.create(UserSearchService.class, config.getBaseUrl());
    }

    @Provides
    ProfileService profileService(JophielClientConfiguration config) {
        return JaxRsClients.create(ProfileService.class, config.getBaseUrl());
    }
}
