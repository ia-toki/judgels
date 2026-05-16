package tlx.uriel.tasks;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Singleton;
import judgels.persistence.ContestClarificationDao;
import judgels.persistence.ContestLogDao;
import judgels.persistence.ContestProblemDao;
import judgels.persistence.ContestProgrammingSubmissionDao;
import judgels.persistence.ProblemDao;

@Module
public class TlxUrielTaskModule {
    private TlxUrielTaskModule() {}

    @Provides
    @Singleton
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
}
