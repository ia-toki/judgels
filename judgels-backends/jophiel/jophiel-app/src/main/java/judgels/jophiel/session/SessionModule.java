package judgels.jophiel.session;

import dagger.Module;
import dagger.Provides;

@Module
public class SessionModule {
    private final SessionConfiguration config;

    public SessionModule(SessionConfiguration config) {
        this.config = config;
    }

    @Provides
    SessionConfiguration sessionConfig() {
        return this.config;
    }
}
