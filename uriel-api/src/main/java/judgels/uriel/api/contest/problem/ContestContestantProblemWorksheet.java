package judgels.uriel.api.contest.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import judgels.sandalphon.api.problem.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestContestantProblemWorksheet.class)
public interface ContestContestantProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ContestContestantProblem getContestantProblem();
    ProblemWorksheet getWorksheet();

    class Builder extends ImmutableContestContestantProblemWorksheet.Builder{}
}
