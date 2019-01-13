package org.iatoki.judgels.sandalphon.sealtiel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.palantir.conjure.java.api.config.service.UserAgent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

public final class SealtielModule extends AbstractModule {
    @Override
    protected void configure() {}

    @Provides
    SealtielConfiguration config() {
        Config config = ConfigFactory.load();
        return new SealtielConfiguration(
                config.getString("sealtiel.baseUrl"),
                config.getString("sealtiel.clientJid"),
                config.getString("sealtiel.clientSecret"),
                config.getString("sealtiel.gabrielClientJid"));
    }

    @Provides
    @SealtielClientAuthHeader
    BasicAuthHeader clientAuthHeader(SealtielConfiguration config) {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    MessageService messageService(SealtielConfiguration config) {
        UserAgent userAgent = UserAgent.of(UserAgent.Agent.of("sandalphon", UserAgent.Agent.DEFAULT_VERSION));
        return JaxRsClients.create(MessageService.class, config.getBaseUrl(), userAgent);
    }
}
