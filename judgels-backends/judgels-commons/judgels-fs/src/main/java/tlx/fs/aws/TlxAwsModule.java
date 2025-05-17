package tlx.fs.aws;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;

@Module
public class TlxAwsModule {
    private final Optional<AwsConfiguration> config;

    public TlxAwsModule(Optional<AwsConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    Optional<AwsConfiguration> awsConfig() {
        return config;
    }
}
