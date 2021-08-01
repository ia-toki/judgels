package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemMetadata.class)
public interface ProblemMetadata {
    boolean hasEditorial();
    Set<String> getTags();
    Map<ProblemSetterRole, List<String>> getSettersMap();

    class Builder extends ImmutableProblemMetadata.Builder {}
}
