package judgels.uriel.contest.log;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;

@Module
public class ContestLogModule {
    private ContestLogModule() {}

    @Provides
    @Singleton
    @Named("ContestLogQueue")
    static Queue<ContestLog> contestLogQueue() {
        return new ConcurrentLinkedQueue<>();
    }

    @Provides
    @Singleton
    static ContestLogPoller contestLogPoller(
            LifecycleEnvironment lifecycleEnvironment,
            @Named("ContestLogQueue") Queue<ContestLog> logQueue,
            ContestLogCreator logCreator) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("contest-log-creator-%d")
                        .maxThreads(2)
                        .minThreads(2)
                        .build();

        return new ContestLogPoller(logQueue, executorService, logCreator);
    }

    @Provides
    @Singleton
    static ContestLogCreator contestLogCreator(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ContestStore contestStore,
            ContestRoleChecker roleChecker,
            ContestLogStore logStore) {

        return unitOfWorkAwareProxyFactory.create(
                ContestLogCreator.class,
                new Class<?>[] {
                        ContestStore.class,
                        ContestRoleChecker.class,
                        ContestLogStore.class},
                new Object[] {
                        contestStore,
                        roleChecker,
                        logStore});
    }
}
