package judgels.sandalphon.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.problem.ProblemSetterRole;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemMetadata.class)
public interface ProblemMetadata {
    boolean hasEditorial();
    Map<ProblemSetterRole, List<String>> getSettersMap();

    class Builder extends ImmutableProblemMetadata.Builder {}
}
