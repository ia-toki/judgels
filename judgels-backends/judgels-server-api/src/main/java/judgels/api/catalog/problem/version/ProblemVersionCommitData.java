package judgels.api.catalog.problem.version;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemVersionCommitData.class)
public interface ProblemVersionCommitData {
    String getTitle();
    String getDescription();

    class Builder extends ImmutableProblemVersionCommitData.Builder {}
}
