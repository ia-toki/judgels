package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemDifficulty.class)
public interface ProblemDifficulty {
    Optional<Integer> getLevel();
    ProblemStats getStats();

    class Builder extends ImmutableProblemDifficulty.Builder {}
}
