package judgels.api.catalog.problem.writer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemWriterData.class)
public interface ProblemWriterData {
    String getUsername();

    class Builder extends ImmutableProblemWriterData.Builder {}
}
