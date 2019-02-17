package judgels.sandalphon.api.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSubmissionConfig.class)
public interface ProblemSubmissionConfig {
    Map<String, String> getSourceKeys();
    String getGradingEngine();
    LanguageRestriction getGradingLanguageRestriction();

    class Builder extends ImmutableProblemSubmissionConfig.Builder {}
}
