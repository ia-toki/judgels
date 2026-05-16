package judgels.submission.bundle;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.sandalphon.SandalphonClient;
import judgels.stats.StatsConfiguration;

@Module
public class TrainingItemSubmissionModule {
    private final StatsConfiguration statsConfig;

    public TrainingItemSubmissionModule(StatsConfiguration statsConfig) {
        this.statsConfig = statsConfig;
    }

    @Provides
    @Singleton
    ItemSubmissionConsumer itemSubmissionConsumer(StatsProcessor statsProcessor) {
        return statsConfig.getEnabled() ? statsProcessor : new NoOpItemSubmissionConsumer();
    }

    @Provides
    @Singleton
    static ItemSubmissionStore itemSubmissionStore(BundleItemSubmissionDao submissionDao) {
        return new BaseItemSubmissionStore<>(submissionDao);
    }

    @Provides
    @Singleton
    static ItemSubmissionRegradeProcessor itemSubmissionRegradeProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            ItemSubmissionStore itemSubmissionStore,
            SandalphonClient sandalphonClient) {

        return unitOfWorkAwareProxyFactory.create(
                ItemSubmissionRegradeProcessor.class,
                new Class<?>[] {
                        ItemSubmissionGraderRegistry.class,
                        ItemSubmissionStore.class,
                        SandalphonClient.class
                },
                new Object[] {
                        itemSubmissionGraderRegistry,
                        itemSubmissionStore,
                        sandalphonClient
                }
        );
    }
}
