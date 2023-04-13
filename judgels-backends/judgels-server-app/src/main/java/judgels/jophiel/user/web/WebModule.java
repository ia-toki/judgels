package judgels.jophiel.user.web;

import dagger.Module;
import dagger.Provides;

@Module
public class WebModule {
    private final WebConfiguration config;

    public WebModule(WebConfiguration config) {
        this.config = config;
    }

    @Provides
    WebConfiguration webConfig() {
        return config;
    }
}
