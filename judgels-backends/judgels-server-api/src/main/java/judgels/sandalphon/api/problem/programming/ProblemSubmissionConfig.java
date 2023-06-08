package judgels.sandalphon.api.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Map;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSubmissionConfig.class)
public interface ProblemSubmissionConfig {
    Map<String, String> getSourceKeys();
    String getGradingEngine();
    LanguageRestriction getGradingLanguageRestriction();
    Instant getGradingLastUpdateTime();

    class Builder extends ImmutableProblemSubmissionConfig.Builder {}
}
