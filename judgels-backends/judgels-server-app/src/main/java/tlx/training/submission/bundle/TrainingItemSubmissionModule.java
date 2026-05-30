package tlx.training.submission.bundle;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import judgels.persistence.dao.BundleItemSubmissionDao;
import judgels.problem.ProblemService;
import judgels.stats.StatsConfiguration;
import judgels.submission.bundle.BaseItemSubmissionStore;
import judgels.submission.bundle.ItemSubmissionConsumer;
import judgels.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.submission.bundle.ItemSubmissionRegradeProcessor;
import judgels.submission.bundle.ItemSubmissionRegrader;
import judgels.submission.bundle.ItemSubmissionStore;
import judgels.submission.bundle.NoOpItemSubmissionConsumer;
import tlx.TlxScope;

@Module
public class TrainingItemSubmissionModule {
    private final StatsConfiguration statsConfig;

    public TrainingItemSubmissionModule(StatsConfiguration statsConfig) {
        this.statsConfig = statsConfig;
    }

    @Provides
    @TlxScope
    ItemSubmissionConsumer itemSubmissionConsumer(StatsProcessor statsProcessor) {
        return statsConfig.getEnabled() ? statsProcessor : new NoOpItemSubmissionConsumer();
    }

    @Provides
    @TlxScope
    @TrainingItemSubmissionStore
    static ItemSubmissionStore itemSubmissionStore(BundleItemSubmissionDao submissionDao) {
        return new BaseItemSubmissionStore<>(submissionDao);
    }

    @Provides
    @TlxScope
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
    @TlxScope
    @TrainingItemSubmissionRegrader
    static ItemSubmissionRegrader itemSubmissionRegrader(
            @TrainingItemSubmissionStore ItemSubmissionStore itemSubmissionStore,
            @TrainingItemSubmissionRegradeProcessor ItemSubmissionRegradeProcessor processor) {
        return new ItemSubmissionRegrader(itemSubmissionStore, processor);
    }
}
