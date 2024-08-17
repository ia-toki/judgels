package judgels.uriel.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import judgels.jophiel.JophielClient;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.submission.bundle.ItemSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.JudgelsScheduler;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.ContestTimer;
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
            JudgelsScheduler scheduler,
            ContestStore contestStore,
            ContestScoreboardUpdater contestScoreboardUpdater) {

        ExecutorService executorService = scheduler.createExecutorService("uriel-contest-scoreboard-updater-%d", 2);

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardPoller.class,
                new Class<?>[] {
                        ContestStore.class,
                        ExecutorService.class,
                        ContestScoreboardUpdater.class},
                new Object[] {
                        contestStore,
                        executorService,
                        contestScoreboardUpdater});
    }

    @Provides
    @Singleton
    static ContestScoreboardUpdater contestScoreboardUpdater(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper objectMapper,
            ContestTimer contestTimer,
            ContestStore contestStore,
            ContestScoreboardStore scoreboardStore,
            ContestModuleStore moduleStore,
            ContestContestantStore contestantStore,
            ContestProblemStore problemStore,
            SubmissionStore programmingSubmissionStore,
            ItemSubmissionStore bundleItemSubmissionStore,
            ScoreboardIncrementalMarker scoreboardIncrementalMarker,
            ScoreboardProcessorRegistry scoreboardProcessorRegistry,
            ContestScoreboardPusher scoreboardPusher,
            JophielClient jophielClient,
            SandalphonClient sandalphonClient) {

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardUpdater.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        ContestTimer.class,
                        ContestStore.class,
                        ContestScoreboardStore.class,
                        ContestModuleStore.class,
                        ContestContestantStore.class,
                        ContestProblemStore.class,
                        SubmissionStore.class,
                        ItemSubmissionStore.class,
                        ScoreboardIncrementalMarker.class,
                        ScoreboardProcessorRegistry.class,
                        ContestScoreboardPusher.class,
                        JophielClient.class,
                        SandalphonClient.class},
                new Object[] {
                        objectMapper,
                        contestTimer,
                        contestStore,
                        scoreboardStore,
                        moduleStore,
                        contestantStore,
                        problemStore,
                        programmingSubmissionStore,
                        bundleItemSubmissionStore,
                        scoreboardIncrementalMarker,
                        scoreboardProcessorRegistry,
                        scoreboardPusher,
                        jophielClient,
                        sandalphonClient});
    }
}
