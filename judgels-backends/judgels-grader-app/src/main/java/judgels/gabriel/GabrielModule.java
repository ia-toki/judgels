package judgels.gabriel;

import dagger.Module;
import dagger.Provides;
import java.time.Clock;
import javax.inject.Singleton;

@Module
public class GabrielModule {
    private GabrielModule() {}

    @Provides
    @Singleton
    static Clock clock() {
        return Clock.systemUTC();
    }
}
