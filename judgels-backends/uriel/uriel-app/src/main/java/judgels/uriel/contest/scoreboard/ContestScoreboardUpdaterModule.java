package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.time.Clock;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;

@Module
public class ContestScoreboardUpdaterModule {
    private ContestScoreboardUpdaterModule() {}

    @Provides
    @Singleton
    static ContestScoreboardPoller contestScoreboardPoller(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            LifecycleEnvironment lifecycleEnvironment,
            ContestStore contestStore,
            ContestScoreboardUpdater scoreboardUpdater) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("contest-scoreboard-updater-%d")
                        .maxThreads(2)
                        .minThreads(2)
                        .build();

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardPoller.class,
                new Class<?>[] {
                        ContestStore.class,
                        ExecutorService.class,
                        ContestScoreboardUpdater.class},
                new Object[] {
                        contestStore,
                        executorService,
                        scoreboardUpdater});
    }

    @Provides
    @Singleton
    static ContestScoreboardUpdater contestScoreboardUpdater(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper objectMapper,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            SubmissionStore programmingSubmissionStore,
            ItemSubmissionStore bundleItemSubmissionStore,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            Clock clock) {

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardUpdater.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        ContestScoreboardStore.class,
                        ContestModuleStore.class,
                        ContestContestantStore.class,
                        ContestProblemStore.class,
                        SubmissionStore.class,
                        ItemSubmissionStore.class,
                        ScoreboardProcessorRegistry.class,
                        Clock.class},
                new Object[] {
                        objectMapper,
                        scoreboardStore,
                        moduleStore,
                        contestantStore,
                        problemStore,
                        programmingSubmissionStore,
                        bundleItemSubmissionStore,
                        scoreboardProcessorRegistry,
                        clock});
    }
}
