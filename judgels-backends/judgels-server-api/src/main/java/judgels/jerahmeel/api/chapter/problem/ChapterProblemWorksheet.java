package judgels.jerahmeel.api.chapter.problem;

import java.util.Optional;
import java.util.Set;

public interface ChapterProblemWorksheet {
    String getDefaultLanguage();
    Set<String> getLanguages();
    ChapterProblem getProblem();
    Optional<String> getPreviousResourcePath();
    Optional<String> getNextResourcePath();
}
