package judgels;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import java.nio.file.Path;
import judgels.core.JudgelsBaseDataDir;
import judgels.rating.RatingConfiguration;
import judgels.training.TrainingConfiguration;

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
    @Named("ratingEnabled")
    boolean ratingEnabled() {
        return config.getRatingConfig().map(RatingConfiguration::getEnabled).orElse(false);
    }

    @Provides
    @Named("trainingEnabled")
    boolean trainingEnabled() {
        return config.getTrainingConfig().map(TrainingConfiguration::getEnabled).orElse(false);
    }
}
