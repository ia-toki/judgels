package judgels.contest.scoreboard;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import judgels.api.contest.scoreboard.ContestScoreboardType;
import org.immutables.value.Value;

@Value.Immutable
public interface ScoreboardIncrementalMark {
    Optional<ScoreboardIncrementalMarkKey> getKey();
    Instant getTimestamp();
    long getLastSubmissionId();
    Map<ContestScoreboardType, ScoreboardIncrementalContent> getIncrementalContents();

    class Builder extends ImmutableScoreboardIncrementalMark.Builder {}
}
