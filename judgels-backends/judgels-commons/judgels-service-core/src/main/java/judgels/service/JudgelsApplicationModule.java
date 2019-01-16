package judgels.service;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;

@Module
public class JudgelsApplicationModule {
    private final Environment environment;

    public JudgelsApplicationModule(Environment environment) {
        this.environment = environment;
    }

    @Provides
    public LifecycleEnvironment lifecycleEnvironment() {
        return environment.lifecycle();
    }
}
