package judgels.training.submission.bundle;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.persistence.BundleItemSubmissionDao;
import judgels.problem.ProblemService;
import judgels.stats.StatsConfiguration;
import judgels.submission.bundle.BaseItemSubmissionStore;
import judgels.submission.bundle.ItemSubmissionConsumer;
import judgels.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.submission.bundle.ItemSubmissionRegradeProcessor;
import judgels.submission.bundle.ItemSubmissionRegrader;
import judgels.submission.bundle.ItemSubmissionStore;
import judgels.submission.bundle.NoOpItemSubmissionConsumer;

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
    @TrainingItemSubmissionStore
    static ItemSubmissionStore itemSubmissionStore(BundleItemSubmissionDao submissionDao) {
        return new BaseItemSubmissionStore<>(submissionDao);
    }

    @Provides
    @Singleton
    @TrainingItemSubmissionRegradeProcessor
    static ItemSubmissionRegradeProcessor itemSubmissionRegradeProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            @TrainingItemSubmissionStore ItemSubmissionStore itemSubmissionStore,
            ProblemService problemService) {

        return unitOfWorkAwareProxyFactory.create(
                ItemSubmissionRegradeProcessor.class,
                new Class<?>[] {
                        ItemSubmissionGraderRegistry.class,
                        ItemSubmissionStore.class,
                        ProblemService.class
                },
                new Object[] {
                        itemSubmissionGraderRegistry,
                        itemSubmissionStore,
                        problemService
                }
        );
    }

    @Provides
    @Singleton
    @TrainingItemSubmissionRegrader
    static ItemSubmissionRegrader itemSubmissionRegrader(
            @TrainingItemSubmissionStore ItemSubmissionStore itemSubmissionStore,
            @TrainingItemSubmissionRegradeProcessor ItemSubmissionRegradeProcessor processor) {
        return new ItemSubmissionRegrader(itemSubmissionStore, processor);
    }
}
