package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemsResponse.class)
public interface ContestProblemsResponse {
    List<ContestProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, Long> getTotalSubmissionsMap();
    ContestProblemConfig getConfig();

    class Builder extends ImmutableContestProblemsResponse.Builder {}
}
