package judgels.uriel.contest.scoreboard.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
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
            ContestScoreboardUpdater contestScoreboardUpdater) {

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardUpdaterDispatcher.class,
                new Class<?>[] {LifecycleEnvironment.class, ContestStore.class, ContestScoreboardUpdater.class},
                new Object[] {lifecycleEnvironment, contestStore, contestScoreboardUpdater});
    }

    @Provides
    static ContestScoreboardUpdater contestScoreboardUpdater(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper objectMapper,
            ContestScoreboardStore contestScoreboardStore,
            ContestModuleStore contestModuleStore,
            ContestContestantStore contestContestantStore,
            ContestProblemStore contestProblemStore,
            ContestSubmissionStore contestSubmissionStore,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry) {

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardUpdater.class,
                new Class<?>[] {ObjectMapper.class, ContestScoreboardStore.class, ContestModuleStore.class,
                                ContestContestantStore.class, ContestProblemStore.class, ContestSubmissionStore.class,
                                ScoreboardProcessorRegistry.class},
                new Object[] {objectMapper, contestScoreboardStore, contestModuleStore, contestContestantStore,
                              contestProblemStore, contestSubmissionStore, scoreboardProcessorRegistry});
    }
}
