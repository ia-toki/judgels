package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.sandalphon.api.problem.programming.ProgrammingProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestProgrammingProblemWorksheet.class)
public interface ContestProgrammingProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ContestProblem getProblem();
    long getTotalSubmissions();
    ProgrammingProblemWorksheet getWorksheet();

    class Builder extends ImmutableContestProgrammingProblemWorksheet.Builder{}
}
