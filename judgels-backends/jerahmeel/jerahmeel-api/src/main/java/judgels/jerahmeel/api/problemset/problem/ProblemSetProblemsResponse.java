package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemStats;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemsResponse.class)
public interface ProblemSetProblemsResponse {
    List<ProblemSetProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, ProblemProgress> getProblemProgressesMap();
    Map<String, ProblemStats> getProblemStatsMap();

    class Builder extends ImmutableProblemSetProblemsResponse.Builder {}
}
