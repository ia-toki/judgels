package judgels.uriel.sealtiel;

import com.palantir.remoting3.clients.UserAgent;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class SealtielModule {
    private final SealtielConfiguration config;

    public SealtielModule(SealtielConfiguration config) {
        this.config = config;
    }

    @Provides
    @SealtielClientAuthHeader
    BasicAuthHeader clientAuthHeader() {
        return BasicAuthHeader.of(Client.of(config.getClientJid(), config.getClientSecret()));
    }

    @Provides
    @Singleton
    MessageService messageService(UserAgent agent) {
        return JaxRsClients.create(MessageService.class, config.getBaseUrl(), agent);
    }
}
