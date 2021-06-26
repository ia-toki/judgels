package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemLevel.class)
public interface ProblemLevel {
    int getLevel();

    class Builder extends ImmutableProblemLevel.Builder {}
}
