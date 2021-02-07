package judgels.jerahmeel.submission.programming;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionService;

@Module
public class StatsModule {
    private StatsModule() {}

    @Provides
    @Singleton
    static ProblemSetStatsTask problemSetStatsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            SubmissionStore submissionStore,
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
            Optional<ContestSubmissionService> contestSubmissionService,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                ContestStatsTask.class,
                new Class<?>[] {
                        Optional.class,
                        StatsProcessor.class},
                new Object[] {
                        contestSubmissionService,
                        statsProcessor});
    }
}
