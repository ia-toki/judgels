package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableScoreboardState.class)
public interface ScoreboardState {
    List<String> getProblemJids();
    List<String> getProblemAliases();
    Optional<List<Integer>> getProblemPoints();
    Set<String> getContestantJids();

    class Builder extends ImmutableScoreboardState.Builder {}
}
