package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.problem.ProblemStatement;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantProblemStatement.class)
public interface ContestContestantProblemStatement {
    ContestProblem getProblem();
    long getTotalSubmissions();
    ProblemStatement getStatement();

    class Builder extends ImmutableContestContestantProblemStatement.Builder{}
}
