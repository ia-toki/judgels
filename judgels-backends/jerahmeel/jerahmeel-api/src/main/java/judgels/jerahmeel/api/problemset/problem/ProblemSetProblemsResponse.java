package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemsResponse.class)
public interface ProblemSetProblemsResponse {
    List<ProblemSetProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();

    class Builder extends ImmutableProblemSetProblemsResponse.Builder {}
}
