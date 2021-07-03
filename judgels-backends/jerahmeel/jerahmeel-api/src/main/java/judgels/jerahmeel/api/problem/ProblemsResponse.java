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
    Map<String, ProblemMetadata> getProblemMetadatasMap();
    Map<String, ProblemDifficulty> getProblemDifficultiesMap();
    Map<String, ProblemProgress> getProblemProgressesMap();

    class Builder extends ImmutableProblemsResponse.Builder {}
}
