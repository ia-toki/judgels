package judgels.uriel.sandalphon;

import com.palantir.remoting3.clients.UserAgent;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.sandalphon.api.client.problem.ClientProblemService;

@Module
public class SandalphonModule {
    private final SandalphonConfiguration config;

    public SandalphonModule(SandalphonConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    ClientProblemService clientProblemService(UserAgent agent) {
        return new ClientProblemResource();
    }
}
