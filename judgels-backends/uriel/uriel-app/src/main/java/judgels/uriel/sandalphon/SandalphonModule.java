package judgels.uriel.sandalphon;

import com.palantir.remoting3.clients.UserAgent;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class SandalphonModule {
    private final SandalphonConfiguration config;

    public SandalphonModule(SandalphonConfiguration config) {
        this.config = config;
    }

    @Provides
    SandalphonConfiguration sandalphonConfig() {
        return config;
    }

    @Provides
    @SandalphonClientAuthHeader
    BasicAuthHeader clientAuthHeader() {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    @Singleton
    ClientProblemService clientProblemService(UserAgent agent) {
        return JaxRsClients.create(ClientProblemService.class, config.getBaseUrl(), agent);
    }
}
