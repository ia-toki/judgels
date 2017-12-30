package judgels.jophiel.web;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class WebModule {
    private final WebConfiguration config;

    public WebModule(WebConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    WebConfiguration webConfiguration() {
        return config;
    }
}
