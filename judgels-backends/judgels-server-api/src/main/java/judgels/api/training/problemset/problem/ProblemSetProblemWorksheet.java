package judgels.api.training.problemset.problem;

import java.util.Set;

public interface ProblemSetProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ProblemSetProblem getProblem();
}
