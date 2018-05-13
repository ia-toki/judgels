package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemInfo.class)
public interface ProblemInfo {
    String getSlug();
    String getName();

    class Builder extends ImmutableProblemInfo.Builder {}
}
