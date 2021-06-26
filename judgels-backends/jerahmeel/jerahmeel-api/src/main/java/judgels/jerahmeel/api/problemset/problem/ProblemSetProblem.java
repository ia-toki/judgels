package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import judgels.sandalphon.api.problem.ProblemType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblem.class)
public interface ProblemSetProblem {
    String getAlias();
    String getProblemJid();
    ProblemType getType();
    List<String> getContestJids();
    Optional<Integer> getLevel();

    class Builder extends ImmutableProblemSetProblem.Builder {}
}
