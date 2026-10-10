package judgels.api.catalog.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemCreateData.class)
public interface ProblemCreateData {
    String getSlug();
    String getGradingEngine();
    String getAdditionalNote();
    String getInitialLanguage();

    class Builder extends ImmutableProblemCreateData.Builder {}
}
