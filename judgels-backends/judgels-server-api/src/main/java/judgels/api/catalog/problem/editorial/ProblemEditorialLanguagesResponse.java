package judgels.api.catalog.problem.editorial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemEditorialLanguagesResponse.class)
public interface ProblemEditorialLanguagesResponse {
    Set<String> getEnabledLanguages();
    Set<String> getDisabledLanguages();
    String getDefaultLanguage();

    class Builder extends ImmutableProblemEditorialLanguagesResponse.Builder {}
}
