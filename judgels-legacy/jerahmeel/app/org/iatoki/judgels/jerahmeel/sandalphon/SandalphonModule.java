package org.iatoki.judgels.jerahmeel.sandalphon;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sandalphon.api.client.lesson.ClientLessonService;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

import javax.inject.Named;

public final class SandalphonModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    SandalphonClientConfiguration config() {
        Config config = ConfigFactory.load();
        return new SandalphonClientConfiguration.Builder()
                .baseUrl(config.getString("sandalphon.baseUrl"))
                .clientJid(config.getString("sandalphon.clientJid"))
                .clientSecret(config.getString("sandalphon.clientSecret"))
                .build();
    }

    @Provides
    @Named("sandalphon")
    BasicAuthHeader clientAuthHeader(SandalphonClientConfiguration config) {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    ClientProblemService clientProblemService(SandalphonClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("jerahmeel", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(ClientProblemService.class, config.getBaseUrl(), userAgent);
    }

    @Provides
    ClientLessonService clientLessonService(SandalphonClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("jerahmeel", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(ClientLessonService.class, config.getBaseUrl(), userAgent);
    }
}
