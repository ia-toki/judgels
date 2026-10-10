package judgels.api.catalog.problem.statement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStatementLanguagesResponse.class)
public interface ProblemStatementLanguagesResponse {
    Set<String> getEnabledLanguages();
    Set<String> getDisabledLanguages();
    String getDefaultLanguage();

    class Builder extends ImmutableProblemStatementLanguagesResponse.Builder {}
}
