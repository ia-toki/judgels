package org.iatoki.judgels.sandalphon.sealtiel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.palantir.conjure.java.api.config.service.UserAgent;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sealtiel.api.SealtielClientConfiguration;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

import javax.inject.Named;

public final class SealtielModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    SealtielClientConfiguration config(SandalphonConfiguration config) {
        return config.getSealtielConfig();
    }

    @Provides
    @Named("sealtiel")
    BasicAuthHeader clientAuthHeader(SealtielClientConfiguration config) {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    MessageService messageService(SealtielClientConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(MessageService.class, config.getBaseUrl(), userAgent);
    }
}
