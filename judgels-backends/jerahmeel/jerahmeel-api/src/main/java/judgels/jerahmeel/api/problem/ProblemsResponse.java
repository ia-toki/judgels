package judgels.jerahmeel.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.ProblemMetadata;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemsResponse.class)
public interface ProblemsResponse {
    Page<ProblemSetProblemInfo> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, ProblemLevel> getProblemLevelsMap();
    Map<String, ProblemMetadata> getProblemMetadatasMap();
    Map<String, ProblemProgress> getProblemProgressesMap();
    Map<String, ProblemStats> getProblemStatsMap();

    class Builder extends ImmutableProblemsResponse.Builder {}
}
