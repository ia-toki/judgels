package judgels.jerahmeel.sandalphon;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Paths;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.sandalphon.Git;
import judgels.sandalphon.LocalGit;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sandalphon.api.client.lesson.ClientLessonService;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.base.ProblemGit;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class SandalphonModule {
    private final String baseDataDir;
    private final SandalphonClientConfiguration config;

    public SandalphonModule(String baseDataDir, SandalphonClientConfiguration config) {
        this.baseDataDir = baseDataDir;
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
    ClientLessonService clientLessonService() {
        return JaxRsClients.create(ClientLessonService.class, config.getBaseUrl());
    }

    @Provides
    @Singleton
    ClientProblemService clientProblemService() {
        return JaxRsClients.create(ClientProblemService.class, config.getBaseUrl());
    }

    @Provides
    @Singleton
    @ProblemFs
    FileSystem problemFs() {
        return new LocalFileSystem(Paths.get(baseDataDir));
    }

    @Provides
    @Singleton
    @ProblemGit
    static Git problemGit(@ProblemFs FileSystem fs) {
        return new LocalGit((LocalFileSystem) fs);
    }
}
