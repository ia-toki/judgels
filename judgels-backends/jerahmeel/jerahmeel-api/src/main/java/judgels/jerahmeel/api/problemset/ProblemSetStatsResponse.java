package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetStatsResponse.class)
public interface ProblemSetStatsResponse {
    ProblemSetProgress getProgress();

    class Builder extends ImmutableProblemSetStatsResponse.Builder {}
}
