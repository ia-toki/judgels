package judgels.contest.scoreboard;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.api.contest.ContestStyle;
import judgels.api.contest.contestant.ContestContestant;
import judgels.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.api.contest.module.StyleModuleConfig;
import org.immutables.value.Value;

@Value.Immutable
public interface ScoreboardIncrementalMarkKey {
    ContestStyle getStyle();
    Instant getBeginTime();
    Duration getDuration();

    Set<ContestContestant> getContestants();
    List<String> getProblemJids();
    Optional<List<Integer>> getProblemPoints();

    StyleModuleConfig getStyleModuleConfig();
    Optional<FrozenScoreboardModuleConfig> getFrozenScoreboardModuleConfig();

    class Builder extends ImmutableScoreboardIncrementalMarkKey.Builder {}
}
