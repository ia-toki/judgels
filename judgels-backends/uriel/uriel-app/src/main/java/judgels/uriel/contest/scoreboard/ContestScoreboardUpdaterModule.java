package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.uriel.contest.ContestGroupStore;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.ContestTimer;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.group.ContestGroupContestStore;
import judgels.uriel.contest.group.ContestGroupScoreboardStore;
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
            ContestGroupStore contestGroupStore,
            ContestScoreboardUpdater contestScoreboardUpdater,
            ContestGroupScoreboardUpdater contestGroupScoreboardUpdater) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("contest-scoreboard-updater-%d")
                        .maxThreads(2)
                        .minThreads(2)
                        .build();

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardPoller.class,
                new Class<?>[] {
                        ContestStore.class,
                        ContestGroupStore.class,
                        ExecutorService.class,
                        ContestScoreboardUpdater.class,
                        ContestGroupScoreboardUpdater.class},
                new Object[] {
                        contestStore,
                        contestGroupStore,
                        executorService,
                        contestScoreboardUpdater,
                        contestGroupScoreboardUpdater});
    }

    @Provides
    @Singleton
    static ContestScoreboardUpdater contestScoreboardUpdater(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper objectMapper,
            ContestTimer contestTimer,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            SubmissionStore programmingSubmissionStore,
            ItemSubmissionStore bundleItemSubmissionStore,
            ScoreboardIncrementalMarker scoreboardIncrementalMarker,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            ContestScoreboardPusher scoreboardPusher) {

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardUpdater.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        ContestTimer.class,
                        ContestScoreboardStore.class,
                        ContestModuleStore.class,
                        ContestContestantStore.class,
                        ContestProblemStore.class,
                        SubmissionStore.class,
                        ItemSubmissionStore.class,
                        ScoreboardIncrementalMarker.class,
                        ScoreboardProcessorRegistry.class,
                        ContestScoreboardPusher.class},
                new Object[] {
                        objectMapper,
                        contestTimer,
                        scoreboardStore,
                        moduleStore,
                        contestantStore,
                        problemStore,
                        programmingSubmissionStore,
                        bundleItemSubmissionStore,
                        scoreboardIncrementalMarker,
                        scoreboardProcessorRegistry,
                        scoreboardPusher});
    }

    @Provides
    @Singleton
    static ContestGroupScoreboardUpdater contestGroupScoreboardUpdater(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper objectMapper,
            ContestStore contestStore,
            ContestModuleStore moduleStore,
            ContestScoreboardStore scoreboardStore,
            ContestGroupContestStore groupContestStore,
            ContestGroupScoreboardStore groupScoreboardStore,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            ContestScoreboardPusher scoreboardPusher) {

        return unitOfWorkAwareProxyFactory.create(
                ContestGroupScoreboardUpdater.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        ContestStore.class,
                        ContestModuleStore.class,
                        ContestScoreboardStore.class,
                        ContestGroupContestStore.class,
                        ContestGroupScoreboardStore.class,
                        ScoreboardProcessorRegistry.class,
                        ContestScoreboardPusher.class},
                new Object[] {
                        objectMapper,
                        contestStore,
                        moduleStore,
                        scoreboardStore,
                        groupContestStore,
                        groupScoreboardStore,
                        scoreboardProcessorRegistry,
                        scoreboardPusher});
    }
}
