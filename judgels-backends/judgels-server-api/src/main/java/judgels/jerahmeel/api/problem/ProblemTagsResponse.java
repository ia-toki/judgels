package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemTagsResponse.class)
public interface ProblemTagsResponse {
    List<ProblemTagCategory> getData();

    class Builder extends ImmutableProblemTagsResponse.Builder {}
}
