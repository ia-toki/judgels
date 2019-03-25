package judgels.uriel.api.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.persistence.api.dump.Dump;
import judgels.uriel.api.contest.problem.ContestProblem;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemDump.class)
public interface ContestProblemDump extends ContestProblem, Dump {

    class Builder extends ImmutableContestProblemDump.Builder {}
}
