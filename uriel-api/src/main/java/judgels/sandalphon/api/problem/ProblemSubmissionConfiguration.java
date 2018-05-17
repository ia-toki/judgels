package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSubmissionConfiguration.class)
public interface ProblemSubmissionConfiguration {
    Map<String, String> getSourceKeys();
    String getGradingEngine();
    LanguageRestriction getGradingLanguageRestriction();

    class Builder extends ImmutableProblemSubmissionConfiguration.Builder {}
}
