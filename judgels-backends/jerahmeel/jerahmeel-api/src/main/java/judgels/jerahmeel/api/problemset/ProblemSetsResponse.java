package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetsResponse.class)
public interface ProblemSetsResponse {
    Page<ProblemSet> getData();

    class Builder extends ImmutableProblemSetsResponse.Builder {}
}
