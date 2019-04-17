package judgels.uriel.api.contest.scoreboard;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableScoreboardState.class)
public interface ScoreboardState {
    List<String> getProblemJids();
    List<String> getProblemAliases();
    Optional<List<Integer>> getProblemPoints();

    class Builder extends ImmutableScoreboardState.Builder {}
}
