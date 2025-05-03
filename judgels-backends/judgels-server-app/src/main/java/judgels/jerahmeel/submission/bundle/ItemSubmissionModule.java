package judgels.jerahmeel.submission.bundle;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.stats.StatsConfiguration;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.submission.bundle.BaseItemSubmissionStore;
import judgels.sandalphon.submission.bundle.ItemSubmissionConsumer;
import judgels.sandalphon.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.sandalphon.submission.bundle.ItemSubmissionRegradeProcessor;
import judgels.sandalphon.submission.bundle.ItemSubmissionRegrader;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.sandalphon.submission.bundle.NoOpItemSubmissionConsumer;
import judgels.service.JudgelsScheduler;

@Module
public class ItemSubmissionModule {
    private final StatsConfiguration statsConfig;

    public ItemSubmissionModule(StatsConfiguration statsConfig) {
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
    static ItemSubmissionRegrader itemSubmissionRegrader(
            JudgelsScheduler scheduler,
            ItemSubmissionStore itemSubmissionStore,
            ItemSubmissionRegradeProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("jerahmeel-item-submission-regrade-processor-%d", 5);
        return new ItemSubmissionRegrader(itemSubmissionStore, executorService, processor);
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
