package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.sandalphon.api.problem.bundle.BundleProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestBundleProblemWorksheet.class)
public interface ContestBundleProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ContestProblem getProblem();
    long getTotalSubmissions();
    BundleProblemWorksheet getWorksheet();

    class Builder extends ImmutableContestBundleProblemWorksheet.Builder{}
}
