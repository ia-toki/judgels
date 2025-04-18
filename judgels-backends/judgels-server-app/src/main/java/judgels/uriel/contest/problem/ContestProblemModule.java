package judgels.uriel.contest.problem;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.inject.Singleton;
import judgels.sandalphon.persistence.ProblemDao;
import judgels.uriel.persistence.ContestLogDao;
import judgels.uriel.persistence.ContestProblemDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;

@Module
public class ContestProblemModule {
    private ContestProblemModule() {}

    @Provides
    @Singleton
    static ReplaceProblemTask replaceProblemTask(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ProblemDao problemDao,
            ContestProblemDao contestProblemDao,
            ContestProgrammingSubmissionDao contestProgrammingSubmissionDao,
            ContestLogDao contestLogDao) {

        return unitOfWorkAwareProxyFactory.create(
                ReplaceProblemTask.class,
                new Class<?>[] {
                        ProblemDao.class,
                        ContestProblemDao.class,
                        ContestProgrammingSubmissionDao.class,
                        ContestLogDao.class},
                new Object[] {
                        problemDao,
                        contestProblemDao,
                        contestProgrammingSubmissionDao,
                        contestLogDao});
    }
}
