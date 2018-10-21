package judgels.sealtiel;

import dagger.Module;
import dagger.Provides;
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
}
