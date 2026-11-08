package judgels.contest.rating;

import dagger.Module;
import dagger.Provides;

@Module
public class ContestRatingModule {
    private final boolean ratingEnabled;

    public ContestRatingModule(boolean ratingEnabled) {
        this.ratingEnabled = ratingEnabled;
    }

    @Provides
    ContestRatingProvider contestRatingProvider() {
        return ratingEnabled ? new TlxContestRatingProvider() : new JudgelsContestRatingProvider();
    }
}
