package judgels.contest.scoreboard;

import java.util.Optional;

public interface ScoreboardIncrementalContent {
    Optional<Long> getLastSubmissionId();
}
