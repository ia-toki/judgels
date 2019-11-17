package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblem.class)
public interface ProblemSetProblem {
    String getAlias();
    String getProblemJid();

    class Builder extends ImmutableProblemSetProblem.Builder {}
}
