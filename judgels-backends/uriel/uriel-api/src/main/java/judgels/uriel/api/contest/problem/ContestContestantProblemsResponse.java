package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantProblemsResponse.class)
public interface ContestContestantProblemsResponse {
    List<ContestContestantProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();

    class Builder extends ImmutableContestContestantProblemsResponse.Builder {}
}
