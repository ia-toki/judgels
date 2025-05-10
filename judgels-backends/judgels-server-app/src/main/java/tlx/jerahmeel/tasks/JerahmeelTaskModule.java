package tlx.jerahmeel.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ProblemSetDao;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.ProgrammingGradingDao;
import judgels.jerahmeel.persistence.ProgrammingSubmissionDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.submission.JerahmeelSubmissionStore;
import judgels.jerahmeel.submission.programming.SubmissionFs;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.sandalphon.submission.programming.SubmissionStore;

@Module
public class JerahmeelTaskModule {
    private JerahmeelTaskModule() {}

    @Provides
    @Singleton
    static DeleteProblemTask deleteProblemTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProgrammingSubmissionDao programmingSubmissionDao,
            ProgrammingGradingDao programmingGradingDao,
            BundleItemSubmissionDao bundleItemSubmissionDao,
            StatsUserProblemDao statsUserProblemDao) {

        return unitOfWorkAwareProxyFactory.create(
                DeleteProblemTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ChapterProblemDao.class,
                        ProblemSetProblemDao.class,
                        ProgrammingSubmissionDao.class,
                        ProgrammingGradingDao.class,
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

    @Provides
    @Singleton
    static MoveProblemToProblemSetTask moveProblemToProblemSetTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ProblemSetDao problemSetDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            ProgrammingSubmissionDao programmingSubmissionDao) {

        return unitOfWorkAwareProxyFactory.create(
                MoveProblemToProblemSetTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ProblemSetDao.class,
                        ChapterProblemDao.class,
                        ProblemSetProblemDao.class,
                        ProgrammingSubmissionDao.class},
                new Object[] {
                        problemDao,
                        problemSetDao,
                        chapterProblemDao,
                        problemSetProblemDao,
                        programmingSubmissionDao});
    }

    @Provides
    @Singleton
    static UploadDuplexSubmissionsToAwsTask uploadDuplexSubmissionsToAwsTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            @SubmissionFs FileSystem submissionFs,
            @JerahmeelSubmissionStore SubmissionStore submissionStore) {

        return unitOfWorkAwareProxyFactory.create(
                UploadDuplexSubmissionsToAwsTask.class,
                new Class<?>[] {
                        FileSystem.class,
                        SubmissionStore.class},
                new Object[] {
                        submissionFs,
                        submissionStore});
    }
}
