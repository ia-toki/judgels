package judgels.contest.scoreboard.bundle;

import java.util.Optional;
import judgels.contest.scoreboard.ScoreboardIncrementalContent;

public class BundleScoreboardIncrementalContent implements ScoreboardIncrementalContent {
    @Override
    public Optional<Long> getLastSubmissionId() {
        return Optional.empty();
    }
}
