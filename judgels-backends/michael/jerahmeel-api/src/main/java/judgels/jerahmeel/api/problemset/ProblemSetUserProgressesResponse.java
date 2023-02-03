package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.jerahmeel.api.problem.ProblemProgress;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetUserProgressesResponse.class)
public interface ProblemSetUserProgressesResponse {
    Map<String, Map<String, Map<String, ProblemProgress>>> getUserProgressesMap();

    class Builder extends ImmutableProblemSetUserProgressesResponse.Builder {}
}
