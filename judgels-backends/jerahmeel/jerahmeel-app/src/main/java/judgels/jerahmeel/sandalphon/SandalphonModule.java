package judgels.jerahmeel.sandalphon;

import com.palantir.conjure.java.api.config.service.UserAgent;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sandalphon.api.client.lesson.ClientLessonService;
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
    ClientLessonService clientLessonService(UserAgent agent) {
        return JaxRsClients.create(ClientLessonService.class, config.getBaseUrl(), agent);
    }

    @Provides
    @Singleton
    ClientProblemService clientProblemService(UserAgent agent) {
        return JaxRsClients.create(ClientProblemService.class, config.getBaseUrl(), agent);
    }
}
