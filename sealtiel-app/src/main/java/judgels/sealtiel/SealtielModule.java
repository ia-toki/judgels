package judgels.sealtiel;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.jackson.Jackson;
import javax.inject.Singleton;
import judgels.service.client.ClientChecker;

@Module
public class SealtielModule {
    private final SealtielConfiguration sealtielConfig;

    public SealtielModule(SealtielConfiguration sealtielConfig) {
        this.sealtielConfig = sealtielConfig;
    }

    @Provides
    @Singleton
    ClientChecker clientChecker() {
        return new ClientChecker(sealtielConfig.getClients());
    }

    @Provides
    @Singleton
    ObjectMapper objectMapper() {
        return Jackson.newObjectMapper();
    }
}
