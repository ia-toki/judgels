package judgels.api.catalog.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemUpdateData.class)
public interface ProblemUpdateData {
    String getSlug();
    String getAdditionalNote();

    class Builder extends ImmutableProblemUpdateData.Builder {}
}
