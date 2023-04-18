package judgels.jerahmeel.submission.bundle;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import judgels.sandalphon.problem.ProblemClient;
import judgels.sandalphon.submission.bundle.ItemSubmissionGraderRegistry;
import judgels.sandalphon.submission.bundle.ItemSubmissionRegradeProcessor;
import judgels.sandalphon.submission.bundle.ItemSubmissionRegrader;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.service.JudgelsScheduler;

@Module
public class ItemSubmissionModule {
    private ItemSubmissionModule() {}

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
            ProblemClient problemClient) {

        return unitOfWorkAwareProxyFactory.create(
                ItemSubmissionRegradeProcessor.class,
                new Class<?>[] {
                        ItemSubmissionGraderRegistry.class,
                        ItemSubmissionStore.class,
                        ProblemClient.class
                },
                new Object[] {
                        itemSubmissionGraderRegistry,
                        itemSubmissionStore,
                        problemClient
                }
        );
    }
}
