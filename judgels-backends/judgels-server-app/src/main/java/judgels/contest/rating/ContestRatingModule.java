package judgels.contest.rating;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import judgels.rating.RatingConfiguration;

@Module
public class ContestRatingModule {
    private final boolean ratingEnabled;

    public ContestRatingModule(Optional<RatingConfiguration> ratingConfig) {
        this.ratingEnabled = ratingConfig.map(RatingConfiguration::getEnabled).orElse(false);
    }

    @Provides
    ContestRatingProvider contestRatingProvider() {
        return ratingEnabled ? new TlxContestRatingProvider() : new JudgelsContestRatingProvider();
    }
}
