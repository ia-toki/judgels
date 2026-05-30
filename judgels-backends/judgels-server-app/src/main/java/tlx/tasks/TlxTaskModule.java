package tlx.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import judgels.contest.submission.programming.ContestSubmissionStore;
import judgels.persistence.dao.BundleItemSubmissionDao;
import judgels.persistence.dao.ChapterDao;
import judgels.persistence.dao.ChapterProblemDao;
import judgels.persistence.dao.ContestClarificationDao;
import judgels.persistence.dao.ContestLogDao;
import judgels.persistence.dao.ContestProblemDao;
import judgels.persistence.dao.ContestProgrammingSubmissionDao;
import judgels.persistence.dao.ProblemDao;
import judgels.persistence.dao.ProblemSetDao;
import judgels.persistence.dao.ProblemSetProblemDao;
import judgels.persistence.dao.StatsUserProblemDao;
import judgels.persistence.dao.TrainingProgrammingGradingDao;
import judgels.persistence.dao.TrainingProgrammingSubmissionDao;
import judgels.submission.programming.SubmissionStore;
import tlx.TlxScope;
import tlx.training.submission.programming.StatsProcessor;
import tlx.training.submission.programming.TrainingSubmissionStore;

@Module
public class TlxTaskModule {
    private TlxTaskModule() {}

    @Provides
    @TlxScope
    static ReplaceProblemTask replaceProblemTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ContestProblemDao contestProblemDao,
            ContestProgrammingSubmissionDao contestProgrammingSubmissionDao,
            ContestClarificationDao contestClarificationDao,
            ContestLogDao contestLogDao) {

        return unitOfWorkAwareProxyFactory.create(
                ReplaceProblemTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ContestProblemDao.class,
                        ContestProgrammingSubmissionDao.class,
                        ContestClarificationDao.class,
                        ContestLogDao.class},
                new Object[] {
                        problemDao,
                        contestProblemDao,
                        contestProgrammingSubmissionDao,
                        contestClarificationDao,
                        contestLogDao});
    }

    @Provides
    @TlxScope
    static DeleteProblemTask deleteProblemTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            TrainingProgrammingSubmissionDao programmingSubmissionDao,
            TrainingProgrammingGradingDao programmingGradingDao,
            BundleItemSubmissionDao bundleItemSubmissionDao,
            StatsUserProblemDao statsUserProblemDao) {

        return unitOfWorkAwareProxyFactory.create(
                DeleteProblemTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ChapterProblemDao.class,
                        ProblemSetProblemDao.class,
                        TrainingProgrammingSubmissionDao.class,
                        TrainingProgrammingGradingDao.class,
                        BundleItemSubmissionDao.class,
                        StatsUserProblemDao.class},
                new Object[] {
                        problemDao,
                        chapterProblemDao,
                        problemSetProblemDao,
                        programmingSubmissionDao,
                        programmingGradingDao,
                        bundleItemSubmissionDao,
                        statsUserProblemDao});
    }

    @Provides
    @TlxScope
    static MoveProblemToChapterTask moveProblemToChapterTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ChapterDao chapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            TrainingProgrammingSubmissionDao programmingSubmissionDao) {

        return unitOfWorkAwareProxyFactory.create(
                MoveProblemToChapterTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ChapterDao.class,
                        ChapterProblemDao.class,
                        ProblemSetProblemDao.class,
                        TrainingProgrammingSubmissionDao.class},
                new Object[] {
                        problemDao,
                        chapterDao,
                        chapterProblemDao,
                        problemSetProblemDao,
                        programmingSubmissionDao});
    }

    @Provides
    @TlxScope
    static MoveProblemToProblemSetTask moveProblemToProblemSetTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ProblemSetDao problemSetDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            TrainingProgrammingSubmissionDao programmingSubmissionDao) {

        return unitOfWorkAwareProxyFactory.create(
                MoveProblemToProblemSetTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ProblemSetDao.class,
                        ChapterProblemDao.class,
                        ProblemSetProblemDao.class,
                        TrainingProgrammingSubmissionDao.class},
                new Object[] {
                        problemDao,
                        problemSetDao,
                        chapterProblemDao,
                        problemSetProblemDao,
                        programmingSubmissionDao});
    }

    @Provides
    @TlxScope
    static RefreshContestStatsTask refreshContestStatsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            @ContestSubmissionStore SubmissionStore submissionStore,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                RefreshContestStatsTask.class,
                new Class<?>[] {
                        SubmissionStore.class,
                        StatsProcessor.class},
                new Object[] {
                        submissionStore,
                        statsProcessor});
    }

    @Provides
    @TlxScope
    static RefreshProblemSetStatsTask refreshProblemSetStatsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            @TrainingSubmissionStore SubmissionStore submissionStore,
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
