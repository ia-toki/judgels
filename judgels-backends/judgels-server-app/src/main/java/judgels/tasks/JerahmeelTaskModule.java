package judgels.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.submission.JerahmeelSubmissionStore;
import judgels.submission.programming.StatsProcessor;
import judgels.submission.programming.SubmissionStore;
import judgels.uriel.UrielClient;

@Module
public class JerahmeelTaskModule {
    private JerahmeelTaskModule() {}

    @Provides
    @Singleton
    static RefreshContestStatsTask refreshContestStatsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            UrielClient urielClient,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                RefreshContestStatsTask.class,
                new Class<?>[] {
                        UrielClient.class,
                        StatsProcessor.class},
                new Object[] {
                        urielClient,
                        statsProcessor});
    }

    @Provides
    @Singleton
    static RefreshProblemSetStatsTask refreshProblemSetStatsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            @JerahmeelSubmissionStore SubmissionStore submissionStore,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                RefreshProblemSetStatsTask.class,
                new Class<?>[] {
                        SubmissionStore.class,
                        StatsProcessor.class},
                new Object[] {
                        submissionStore,
                        statsProcessor});
    }

}
