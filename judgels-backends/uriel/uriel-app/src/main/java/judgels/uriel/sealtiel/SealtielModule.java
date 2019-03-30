package judgels.uriel.sealtiel;

import com.palantir.conjure.java.api.config.service.UserAgent;
import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.sealtiel.api.SealtielClientConfiguration;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class SealtielModule {
    private final Optional<SealtielClientConfiguration> sealtielClientConfig;

    public SealtielModule(Optional<SealtielClientConfiguration> config) {
        this.sealtielClientConfig = config;
    }

    @Provides
    SealtielClientConfiguration config() {
        return sealtielClientConfig.orElse(SealtielClientConfiguration.DEFAULT);
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
