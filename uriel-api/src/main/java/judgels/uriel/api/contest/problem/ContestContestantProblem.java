package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantProblem.class)
public interface ContestContestantProblem {
    ContestProblem getProblem();
    long getTotalSubmissions();

    class Builder extends ImmutableContestContestantProblem.Builder {}
}
