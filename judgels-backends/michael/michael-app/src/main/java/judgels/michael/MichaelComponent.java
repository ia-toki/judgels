package judgels.michael;

import dagger.Component;
import javax.inject.Singleton;
import judgels.service.JudgelsApplicationModule;
import judgels.service.JudgelsScheduler;

@Component(modules = JudgelsApplicationModule.class)
@Singleton
public interface MichaelComponent {
    PingResource pingResource();

    JudgelsScheduler scheduler();
}
