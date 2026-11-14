package judgels;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import java.nio.file.Path;
import judgels.core.JudgelsBaseDataDir;
import judgels.training.TrainingConfiguration;
import judgels.user.rating.UserRatingConfiguration;

@Module
public class JudgelsServerModule {
    private final JudgelsServerConfiguration config;

    public JudgelsServerModule(JudgelsServerConfiguration config) {
        this.config = config;
    }

    @Provides
    @JudgelsBaseDataDir
    Path baseDataDir() {
        return config.getBaseDataDir();
    }

    @Provides
    @Named("trainingEnabled")
    boolean trainingEnabled() {
        return config.getTrainingConfig().map(TrainingConfiguration::getEnabled).orElse(false);
    }

    @Provides
    @Named("ratingEnabled")
    boolean ratingEnabled() {
        return config.getUserRatingConfig().map(UserRatingConfiguration::getEnabled).orElse(false);
    }
}
