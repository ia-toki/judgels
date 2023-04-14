package judgels.gabriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.grading.GradingRequestPoller;
import judgels.gabriel.messaging.MessagingModule;
import judgels.gabriel.moe.MoeModule;
import judgels.service.JudgelsModule;

@Component(modules = {
        JudgelsModule.class,
        GabrielModule.class,
        GradingModule.class,
        MessagingModule.class,
        MoeModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();
}
