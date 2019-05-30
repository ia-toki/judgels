package judgels.gabriel.sealtiel;

import com.palantir.conjure.java.api.config.service.UserAgent;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.sealtiel.api.SealtielClientConfiguration;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class SealtielModule {
    private final SealtielClientConfiguration sealtielClientConfig;

    public SealtielModule(SealtielClientConfiguration config) {
        this.sealtielClientConfig = config;
    }

    @Provides
    SealtielClientConfiguration config() {
        return sealtielClientConfig;
    }

    @Provides
    @Named("sealtiel")
    BasicAuthHeader clientAuthHeader(SealtielClientConfiguration config) {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    @Singleton
    MessageService messageService(SealtielClientConfiguration config, UserAgent agent) {
        return JaxRsClients.create(MessageService.class, config.getBaseUrl(), agent);
    }
}
