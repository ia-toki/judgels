package judgels.fs.aws;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;

@Module
public class AwsModule {
    private final Optional<AwsConfiguration> config;

    public AwsModule(Optional<AwsConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    Optional<AwsConfiguration> awsConfig() {
        return config;
    }
}
