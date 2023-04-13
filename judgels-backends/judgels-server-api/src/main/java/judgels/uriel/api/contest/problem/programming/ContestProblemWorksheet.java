package judgels.uriel.api.contest.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.sandalphon.api.problem.programming.ProblemWorksheet;
import judgels.uriel.api.contest.problem.ContestProblem;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProblemWorksheet.class)
public interface ContestProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ContestProblem getProblem();
    long getTotalSubmissions();
    ProblemWorksheet getWorksheet();

    class Builder extends ImmutableContestProblemWorksheet.Builder{}
}
