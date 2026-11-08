package judgels.user.web;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;

@Module
public class UserWebModule {
    private final boolean trainingEnabled;
    private final boolean ratingEnabled;

    public UserWebModule(boolean trainingEnabled, boolean ratingEnabled) {
        this.trainingEnabled = trainingEnabled;
        this.ratingEnabled = ratingEnabled;
    }

    @Provides
    @Named("trainingEnabled")
    boolean trainingEnabled() {
        return trainingEnabled;
    }

    @Provides
    @Named("ratingEnabled")
    boolean ratingEnabled() {
        return ratingEnabled;
    }
}
