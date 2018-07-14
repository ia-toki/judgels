package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStatement.class)
public interface ProblemStatement {
    String getName();
    int getTimeLimit();
    int getMemoryLimit();
    String getText();

    class Builder extends ImmutableProblemStatement.Builder {}
}
