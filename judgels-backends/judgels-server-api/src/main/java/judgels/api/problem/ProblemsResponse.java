package judgels.api.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.persistence.api.Page;
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
