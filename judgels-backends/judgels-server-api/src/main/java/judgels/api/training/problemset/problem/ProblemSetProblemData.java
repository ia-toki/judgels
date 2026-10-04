package judgels.api.training.problemset.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.problem.ProblemType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemSetProblemData.class)
public interface ProblemSetProblemData {
    String getAlias();
    String getSlug();
    ProblemType getType();
    List<String> getContestSlugs();

    class Builder extends ImmutableProblemSetProblemData.Builder {}
}
