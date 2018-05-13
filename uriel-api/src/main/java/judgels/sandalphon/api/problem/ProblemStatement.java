package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStatement.class)
public interface ProblemStatement {
    String getName();
    int getTimeLimit();
    int getMemoryLimit();
    String getText();
    Map<String, String> getSourceKeys();
    String getGradingEngine();
    LanguageRestriction getGradingLanguageRestriction();

    class Builder extends ImmutableProblemStatement.Builder {}
}
