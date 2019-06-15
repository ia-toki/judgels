package judgels.uriel.contest.scoreboard;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.api.contest.module.StyleModuleConfig;
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
