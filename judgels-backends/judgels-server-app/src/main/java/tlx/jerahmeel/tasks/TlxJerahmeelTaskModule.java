package tlx.jerahmeel.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.persistence.BundleItemSubmissionDao;
import judgels.persistence.ChapterDao;
import judgels.persistence.ChapterProblemDao;
import judgels.persistence.ProblemDao;
import judgels.persistence.ProblemSetDao;
import judgels.persistence.ProblemSetProblemDao;
import judgels.persistence.StatsUserProblemDao;
import judgels.persistence.TrainingProgrammingGradingDao;
import judgels.persistence.TrainingProgrammingSubmissionDao;

@Module
public class TlxJerahmeelTaskModule {
    private TlxJerahmeelTaskModule() {}

    @Provides
    @Singleton
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
    @Singleton
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
    @Singleton
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
}
