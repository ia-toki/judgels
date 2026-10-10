package judgels.api.catalog.problem.editorial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemEditorialCreateData.class)
public interface ProblemEditorialCreateData {
    String getInitialLanguage();

    class Builder extends ImmutableProblemEditorialCreateData.Builder {}
}
