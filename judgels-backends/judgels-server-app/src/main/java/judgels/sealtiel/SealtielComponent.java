package judgels.sealtiel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.sealtiel.tasks.ReceiveGradingRequestTask;
import judgels.sealtiel.tasks.SendGradingResponseTask;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;
import judgels.service.JudgelsSchedulerModule;

@Component(modules = {
        JudgelsModule.class,
        JudgelsSchedulerModule.class})
@Singleton
public interface SealtielComponent {
    JudgelsScheduler scheduler();

    ReceiveGradingRequestTask receiveGradingRequestTask();
    SendGradingResponseTask sendGradingResponseTask();
}
