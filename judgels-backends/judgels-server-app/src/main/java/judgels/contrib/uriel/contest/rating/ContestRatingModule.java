package judgels.contrib.uriel.contest.rating;

import dagger.Module;
import dagger.Provides;

@Module
public class ContestRatingModule {
    private final ContestRatingProvider provider;

    public ContestRatingModule() {
        this.provider = new JudgelsContestRatingProvider();
    }

    public ContestRatingModule(ContestRatingProvider provider) {
        this.provider = provider;
    }

    @Provides
    ContestRatingProvider contestRatingProvider() {
        return provider;
    }
}
