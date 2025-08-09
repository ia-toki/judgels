package judgels.uriel.contest.log;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import judgels.service.JudgelsScheduler;
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
            JudgelsScheduler scheduler,
            @Named("ContestLogQueue") Queue<ContestLog> logQueue,
            ContestLogCreator logCreator) {

        ExecutorService executorService = scheduler.createExecutorService("uriel-contest-log-creator-%d", 2);
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
