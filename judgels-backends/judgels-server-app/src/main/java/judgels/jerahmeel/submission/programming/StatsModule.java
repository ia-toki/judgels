package judgels.jerahmeel.submission.programming;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.jerahmeel.submission.JerahmeelSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.UrielClient;

@Module
public class StatsModule {
    private StatsModule() {}

    @Provides
    @Singleton
    static ProblemSetStatsTask problemSetStatsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            @JerahmeelSubmissionStore SubmissionStore submissionStore,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                ProblemSetStatsTask.class,
                new Class<?>[] {
                        SubmissionStore.class,
                        StatsProcessor.class},
                new Object[] {
                        submissionStore,
                        statsProcessor});
    }

    @Provides
    @Singleton
    static ContestStatsTask contestStatsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            UrielClient urielClient,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                ContestStatsTask.class,
                new Class<?>[] {
                        UrielClient.class,
                        StatsProcessor.class},
                new Object[] {
                        urielClient,
                        statsProcessor});
    }
}
