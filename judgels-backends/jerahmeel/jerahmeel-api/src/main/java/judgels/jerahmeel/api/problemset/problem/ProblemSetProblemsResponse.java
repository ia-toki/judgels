package judgels.jerahmeel.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemStats;
import judgels.sandalphon.api.ProblemMetadata;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.uriel.api.contest.ContestInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemsResponse.class)
public interface ProblemSetProblemsResponse {
    List<ProblemSetProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, ProblemMetadata> getProblemMetadatasMap();
    Map<String, ProblemProgress> getProblemProgressesMap();
    Map<String, ProblemStats> getProblemStatsMap();
    Map<String, ContestInfo> getContestsMap();

    class Builder extends ImmutableProblemSetProblemsResponse.Builder {}
}
