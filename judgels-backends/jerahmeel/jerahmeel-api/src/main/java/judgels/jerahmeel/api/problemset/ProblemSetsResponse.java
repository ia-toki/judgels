package judgels.jerahmeel.api.problemset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetsResponse.class)
public interface ProblemSetsResponse {
    Page<ProblemSet> getData();
    Map<String, String> getArchiveDescriptionsMap();

    class Builder extends ImmutableProblemSetsResponse.Builder {}
}
