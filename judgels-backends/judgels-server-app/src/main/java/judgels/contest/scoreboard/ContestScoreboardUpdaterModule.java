package judgels.contest.scoreboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import java.util.concurrent.ExecutorService;
import judgels.contest.ContestStore;
import judgels.contest.ContestTimer;
import judgels.contest.contestant.ContestContestantStore;
import judgels.contest.module.ContestModuleStore;
import judgels.contest.problem.ContestProblemStore;
import judgels.problem.ProblemService;
import judgels.profile.ProfileStore;
import judgels.service.JudgelsScheduler;
import judgels.submission.bundle.ItemSubmissionStore;
import judgels.submission.programming.SubmissionStore;
import org.hibernate.SessionFactory;

@Module
public class ContestScoreboardUpdaterModule {
    private ContestScoreboardUpdaterModule() {}

    @Provides
    @Singleton
    static ContestScoreboardPoller contestScoreboardPoller(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            JudgelsScheduler scheduler,
            SessionFactory sessionFactory,
            ContestStore contestStore,
            ContestScoreboardUpdater contestScoreboardUpdater) {

        ExecutorService executorService = scheduler.createExecutorService("uriel-contest-scoreboard-updater-%d", 2);

        return unitOfWorkAwareProxyFactory.create(
                ContestScoreboardPoller.class,
                new Class<?>[] {
                        SessionFactory.class,
                        ContestStore.class,
                        ExecutorService.class,
                        ContestScoreboardUpdater.class},
                new Object[] {
                        sessionFactory,
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
            ProfileStore profileStore,
            ProblemService problemService) {

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
                        ProfileStore.class,
                        ProblemService.class},
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
                        profileStore,
                        problemService});
    }
}
