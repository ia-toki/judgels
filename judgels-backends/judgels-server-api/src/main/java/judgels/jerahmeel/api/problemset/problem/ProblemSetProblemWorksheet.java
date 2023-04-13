package judgels.jerahmeel.api.problemset.problem;

import java.util.Set;

public interface ProblemSetProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ProblemSetProblem getProblem();
}
