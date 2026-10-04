package judgels.api.training.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import judgels.api.catalog.problem.ProblemInfo;
import judgels.api.catalog.problem.ProblemMetadata;
import judgels.api.training.stats.ProblemDifficulty;
import judgels.api.training.stats.ProblemProgress;
import judgels.persistence.api.Page;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingProblemsResponse.class)
public interface TrainingProblemsResponse {
    Page<ProblemSetProblemInfo> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, ProblemMetadata> getProblemMetadatasMap();
    Map<String, ProblemDifficulty> getProblemDifficultiesMap();
    Map<String, ProblemProgress> getProblemProgressesMap();

    class Builder extends ImmutableTrainingProblemsResponse.Builder {}
}
