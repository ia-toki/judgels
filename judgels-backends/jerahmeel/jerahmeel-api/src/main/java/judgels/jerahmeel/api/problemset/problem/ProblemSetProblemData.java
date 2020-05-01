package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.problem.ProblemType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemData.class)
public interface ProblemSetProblemData {
    String getAlias();
    String getSlug();
    ProblemType getType();

    class Builder extends ImmutableProblemSetProblemData.Builder {}
}
