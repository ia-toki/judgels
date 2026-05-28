package tlx.api.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.contest.ContestInfo;
import judgels.api.problem.ProblemDifficulty;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemMetadata;
import judgels.api.problem.ProblemProgress;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemsResponse.class)
public interface ProblemSetProblemsResponse {
    List<ProblemSetProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, ProblemMetadata> getProblemMetadatasMap();
    Map<String, ProblemDifficulty> getProblemDifficultiesMap();
    Map<String, ProblemProgress> getProblemProgressesMap();
    Map<String, ContestInfo> getContestsMap();

    class Builder extends ImmutableProblemSetProblemsResponse.Builder {}
}
