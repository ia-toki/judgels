package judgels.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.contest.submission.programming.ContestSubmissionStore;
import judgels.persistence.dao.BundleItemSubmissionDao;
import judgels.persistence.dao.ChapterDao;
import judgels.persistence.dao.ChapterProblemDao;
import judgels.persistence.dao.ContestAnnouncementDao;
import judgels.persistence.dao.ContestClarificationDao;
import judgels.persistence.dao.ContestContestantDao;
import judgels.persistence.dao.ContestDao;
import judgels.persistence.dao.ContestLogDao;
import judgels.persistence.dao.ContestManagerDao;
import judgels.persistence.dao.ContestModuleDao;
import judgels.persistence.dao.ContestProblemDao;
import judgels.persistence.dao.ContestProgrammingGradingDao;
import judgels.persistence.dao.ContestProgrammingSubmissionDao;
import judgels.persistence.dao.ContestScoreboardDao;
import judgels.persistence.dao.ContestSupervisorDao;
import judgels.persistence.dao.ProblemDao;
import judgels.persistence.dao.ProblemSetDao;
import judgels.persistence.dao.ProblemSetProblemDao;
import judgels.persistence.dao.StatsUserProblemDao;
import judgels.persistence.dao.TrainingProgrammingGradingDao;
import judgels.persistence.dao.TrainingProgrammingSubmissionDao;
import judgels.submission.programming.SubmissionStore;
import judgels.training.submission.programming.StatsProcessor;
import judgels.training.submission.programming.TrainingSubmissionStore;

@Module
public class JudgelsServerTaskModule {
    private JudgelsServerTaskModule() {}

    @Provides
    @Singleton
    static DumpContestTask dumpContestTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ContestDao contestDao,
            ContestModuleDao moduleDao,
            ContestManagerDao managerDao,
            ContestSupervisorDao supervisorDao,
            ContestContestantDao contestantDao,
            ContestProblemDao problemDao,
            ContestAnnouncementDao announcementDao,
            ContestClarificationDao clarificationDao,
            ContestScoreboardDao scoreboardDao,
            ContestLogDao logDao,
            ContestProgrammingSubmissionDao programmingSubmissionDao,
            ContestProgrammingGradingDao programmingGradingDao) {

        return unitOfWorkAwareProxyFactory.create(
                DumpContestTask.class,
                new Class<?>[] {
                        ContestDao.class,
                        ContestModuleDao.class,
                        ContestManagerDao.class,
                        ContestSupervisorDao.class,
                        ContestContestantDao.class,
                        ContestProblemDao.class,
                        ContestAnnouncementDao.class,
                        ContestClarificationDao.class,
                        ContestScoreboardDao.class,
                        ContestLogDao.class,
                        ContestProgrammingSubmissionDao.class,
                        ContestProgrammingGradingDao.class},
                new Object[] {
                        contestDao,
                        moduleDao,
                        managerDao,
                        supervisorDao,
                        contestantDao,
                        problemDao,
                        announcementDao,
                        clarificationDao,
                        scoreboardDao,
                        logDao,
                        programmingSubmissionDao,
                        programmingGradingDao});
    }

    @Provides
    @Singleton
    static ReplaceContestProblemTask replaceContestProblemTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ContestProblemDao contestProblemDao,
            ContestProgrammingSubmissionDao contestProgrammingSubmissionDao,
            ContestClarificationDao contestClarificationDao,
            ContestLogDao contestLogDao) {

        return unitOfWorkAwareProxyFactory.create(
                ReplaceContestProblemTask.class,
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
    @Singleton
    static DeleteTrainingProblemTask deleteTrainingProblemTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            TrainingProgrammingSubmissionDao programmingSubmissionDao,
            TrainingProgrammingGradingDao programmingGradingDao,
            BundleItemSubmissionDao bundleItemSubmissionDao,
            StatsUserProblemDao statsUserProblemDao) {

        return unitOfWorkAwareProxyFactory.create(
                DeleteTrainingProblemTask.class,
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
    @Singleton
    static MoveTrainingProblemToChapterTask moveTrainingProblemToChapterTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ChapterDao chapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            TrainingProgrammingSubmissionDao programmingSubmissionDao) {

        return unitOfWorkAwareProxyFactory.create(
                MoveTrainingProblemToChapterTask.class,
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
    @Singleton
    static MoveTrainingProblemToProblemSetTask moveTrainingProblemToProblemSetTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ProblemSetDao problemSetDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            TrainingProgrammingSubmissionDao programmingSubmissionDao) {

        return unitOfWorkAwareProxyFactory.create(
                MoveTrainingProblemToProblemSetTask.class,
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
    @Singleton
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
    @Singleton
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
