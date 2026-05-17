package judgels.submission.bundle;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.problem.ProblemService;

@Module
public class ContestItemSubmissionModule {
    private ContestItemSubmissionModule() {}

    @Provides
    @Singleton
    @ContestItemSubmissionRegradeProcessor
    static ItemSubmissionRegradeProcessor itemSubmissionRegradeProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ItemSubmissionGraderRegistry itemSubmissionGraderRegistry,
            @ContestItemSubmissionStore ItemSubmissionStore itemSubmissionStore,
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
    @ContestItemSubmissionRegrader
    static ItemSubmissionRegrader itemSubmissionRegrader(
            @ContestItemSubmissionStore ItemSubmissionStore itemSubmissionStore,
            @ContestItemSubmissionRegradeProcessor ItemSubmissionRegradeProcessor processor) {
        return new ItemSubmissionRegrader(itemSubmissionStore, processor);
    }
}
