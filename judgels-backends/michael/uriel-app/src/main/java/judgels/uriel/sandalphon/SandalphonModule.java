package judgels.uriel.sandalphon;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class SandalphonModule {
    private final SandalphonClientConfiguration config;

    public SandalphonModule(SandalphonClientConfiguration config) {
        this.config = config;
    }

    @Provides
    SandalphonClientConfiguration sandalphonConfig() {
        return config;
    }

    @Provides
    @Named("sandalphon")
    BasicAuthHeader clientAuthHeader() {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    @Singleton
    ClientProblemService clientProblemService() {
        return JaxRsClients.create(ClientProblemService.class, config.getBaseUrl());
    }
}
