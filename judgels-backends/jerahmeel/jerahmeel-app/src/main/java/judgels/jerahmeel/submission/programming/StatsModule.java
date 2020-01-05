package judgels.jerahmeel.submission.programming;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.sandalphon.submission.programming.SubmissionStore;

@Module
public class StatsModule {
    private StatsModule() {}

    @Provides
    @Singleton
    static StatsTask statsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            SubmissionStore submissionStore,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                StatsTask.class,
                new Class<?>[] {
                        SubmissionStore.class,
                        StatsProcessor.class},
                new Object[] {
                        submissionStore,
                        statsProcessor});
    }
}
