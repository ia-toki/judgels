package judgels.jerahmeel.api.chapter.problem;

import java.util.Set;

public interface ChapterProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ChapterProblem getProblem();
}
