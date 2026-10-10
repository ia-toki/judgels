package judgels.api.catalog.problem.programming.grading;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.problem.ProblemFile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemGradingFilesResponse.class)
public interface ProblemGradingFilesResponse {
    List<ProblemFile> getData();

    class Builder extends ImmutableProblemGradingFilesResponse.Builder {}
}
