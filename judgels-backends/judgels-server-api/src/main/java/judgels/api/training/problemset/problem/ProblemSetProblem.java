package judgels.api.training.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.problem.ProblemType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblem.class)
public interface ProblemSetProblem {
    String getAlias();
    String getProblemJid();
    ProblemType getType();
    List<String> getContestJids();

    class Builder extends ImmutableProblemSetProblem.Builder {}
}
