package judgels.gabriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.grading.GradingRequestPoller;
import judgels.gabriel.messaging.MessagingModule;
import judgels.gabriel.moe.MoeModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.JudgelsModule;
import judgels.service.JudgelsScheduler;

@Component(modules = {
        JudgelsModule.class,
        JudgelsApplicationModule.class,
        GabrielModule.class,
        GradingModule.class,
        MessagingModule.class,
        MoeModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();

    JudgelsScheduler scheduler();
}
