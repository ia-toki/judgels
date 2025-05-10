package judgels.uriel.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.uriel.persistence.ContestAnnouncementDao;
import judgels.uriel.persistence.ContestClarificationDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestModuleDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProgrammingGradingDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;
import judgels.uriel.persistence.ContestScoreboardDao;
import judgels.uriel.persistence.ContestSupervisorDao;

@Module
public class UrielTaskModule {
    private UrielTaskModule() {}

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

}
