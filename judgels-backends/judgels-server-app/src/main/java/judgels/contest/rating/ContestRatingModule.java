package judgels.contest.rating;

import dagger.Module;
import dagger.Provides;
import judgels.app.JudgelsApp;
import tlx.contest.rating.TlxContestRatingProvider;

@Module
public class ContestRatingModule {
    @Provides
    ContestRatingProvider contestRatingProvider() {
        return JudgelsApp.isTLX() ? new TlxContestRatingProvider() : new JudgelsContestRatingProvider();
    }
}
