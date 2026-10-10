package judgels.api.training.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.problem.tag.ProblemTagCategory;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingProblemTagsResponse.class)
public interface TrainingProblemTagsResponse {
    List<ProblemTagCategory> getData();

    class Builder extends ImmutableTrainingProblemTagsResponse.Builder {}
}
