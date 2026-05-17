package judgels.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.contest.submission.programming.ContestSubmissionStore;
import judgels.persistence.ContestAnnouncementDao;
import judgels.persistence.ContestClarificationDao;
import judgels.persistence.ContestContestantDao;
import judgels.persistence.ContestDao;
import judgels.persistence.ContestLogDao;
import judgels.persistence.ContestManagerDao;
import judgels.persistence.ContestModuleDao;
import judgels.persistence.ContestProblemDao;
import judgels.persistence.ContestProgrammingGradingDao;
import judgels.persistence.ContestProgrammingSubmissionDao;
import judgels.persistence.ContestScoreboardDao;
import judgels.persistence.ContestSupervisorDao;
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
