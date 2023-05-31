package judgels.gabriel.cache;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class CacheModule {
    private final CacheConfiguration config;

    public CacheModule(CacheConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    ProblemCache problemCache() {
        return new ProblemCache(config);
    }
}
