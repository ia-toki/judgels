package judgels.uriel.contest.scoreboard.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.time.Clock;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.scoreboard.ContestScoreboardStore;
import judgels.uriel.contest.scoreboard.ScoreboardProcessorRegistry;
import judgels.uriel.contest.submission.ContestSubmissionStore;

@Module
public class ContestScoreboardUpdaterModule {
    private ContestScoreboardUpdaterModule() {}

    @Provides
    @Singleton
    static ContestScoreboardUpdaterDispatcher contestScoreboardUpdaterDispatcher(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            LifecycleEnvironment lifecycleEnvironment,
            ContestStore contestStore,
            ContestScoreboardUpdater scoreboardUpdater) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService(ContestScoreboardUpdater.class.getName() + "-%d")
                        .maxThreads(ContestScoreboardUpdaterDispatcher.THREAD_NUMBER)
                        .minThreads(ContestScoreboardUpdaterDispatcher.THREAD_NUMBER)
                        .build();

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardUpdaterDispatcher.class,
                new Class<?>[] {ContestStore.class, ExecutorService.class, ContestScoreboardUpdater.class},
                new Object[] {contestStore, executorService, scoreboardUpdater});
    }

    @Provides
    static ContestScoreboardUpdater contestScoreboardUpdater(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper objectMapper,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            ContestSubmissionStore submissionStore,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            Clock clock) {

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardUpdater.class,
                new Class<?>[] {ObjectMapper.class, ContestScoreboardStore.class, ContestModuleStore.class,
                                ContestContestantStore.class, ContestProblemStore.class, ContestSubmissionStore.class,
                                ScoreboardProcessorRegistry.class, Clock.class},
                new Object[] {objectMapper, scoreboardStore, moduleStore, contestantStore,
                              problemStore, submissionStore, scoreboardProcessorRegistry, clock});
    }
}
