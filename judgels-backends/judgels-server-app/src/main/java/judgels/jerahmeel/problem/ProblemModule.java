package judgels.jerahmeel.problem;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProgrammingGradingDao;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.sandalphon.persistence.ProblemDao;

@Module
public class ProblemModule {
    private ProblemModule() {}

    @Provides
    @Singleton
    static DeleteProblemTask deleteProblemTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProgrammingSubmissionDao programmingSubmissionDao,
            ProgrammingGradingDao programmingGradingDao,
            StatsUserProblemDao statsUserProblemDao) {

        return unitOfWorkAwareProxyFactory.create(
                DeleteProblemTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ChapterProblemDao.class,
                        ProblemSetProblemDao.class,
                        ProgrammingSubmissionDao.class,
                        ProgrammingGradingDao.class,
                        StatsUserProblemDao.class},
                new Object[] {
                        problemDao,
                        chapterProblemDao,
                        problemSetProblemDao,
                        programmingSubmissionDao,
                        programmingGradingDao,
                        statsUserProblemDao});
    }

    @Provides
    @Singleton
    static MoveProblemToChapterTask problemMoveToChapterTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ChapterDao chapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProgrammingSubmissionDao programmingSubmissionDao) {

        return unitOfWorkAwareProxyFactory.create(
                MoveProblemToChapterTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ChapterDao.class,
                        ChapterProblemDao.class,
                        ProblemSetProblemDao.class,
                        ProgrammingSubmissionDao.class},
                new Object[] {
                        problemDao,
                        chapterDao,
                        chapterProblemDao,
                        problemSetProblemDao,
                        programmingSubmissionDao});
    }
}
