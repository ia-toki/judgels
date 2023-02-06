package judgels.sandalphon.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStatement.class)
public interface ProblemStatement {
    String getTitle();
    String getText();

    class Builder extends ImmutableProblemStatement.Builder {}
}
