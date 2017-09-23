package judgels.sealtiel;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.jackson.Jackson;
import javax.inject.Singleton;
import judgels.client.ClientChecker;

@Module
public class SealtielModule {
    private final SealtielConfiguration sealtielConfig;

    public SealtielModule(SealtielConfiguration sealtielConfig) {
        this.sealtielConfig = sealtielConfig;
    }

    @Provides
    @Singleton
    public ClientChecker clientChecker() {
        return new ClientChecker(sealtielConfig.getClients());
    }

    @Provides
    @Singleton
    public ObjectMapper objectMapper() {
        return Jackson.newObjectMapper();
    }
}
