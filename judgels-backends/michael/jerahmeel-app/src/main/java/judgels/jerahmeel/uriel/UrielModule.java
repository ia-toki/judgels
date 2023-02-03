package judgels.jerahmeel.uriel;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import judgels.service.jaxrs.JaxRsClients;
import judgels.uriel.api.UrielClientConfiguration;
import judgels.uriel.api.contest.ContestService;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionService;

@Module
public class UrielModule {
    private final Optional<UrielClientConfiguration> config;

    public UrielModule(Optional<UrielClientConfiguration> config) {
        this.config = config;
    }

    @Provides
    Optional<ContestService> contestService() {
        return config.map(cfg -> JaxRsClients.create(ContestService.class, cfg.getBaseUrl()));
    }

    @Provides
    Optional<ContestSubmissionService> contestSubmissionService() {
        return config.map(cfg -> JaxRsClients.create(ContestSubmissionService.class, cfg.getBaseUrl()));
    }
}
