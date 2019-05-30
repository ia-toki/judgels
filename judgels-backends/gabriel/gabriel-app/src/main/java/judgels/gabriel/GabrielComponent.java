package judgels.gabriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.grading.GradingRequestPoller;
import judgels.gabriel.moe.MoeModule;
import judgels.gabriel.sealtiel.SealtielModule;
import judgels.service.JudgelsApplicationModule;
import judgels.service.JudgelsScheduler;

@Component(modules = {
        JudgelsApplicationModule.class,
        GabrielModule.class,
        GradingModule.class,
        MoeModule.class,
        SealtielModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();

    JudgelsScheduler scheduler();
}
