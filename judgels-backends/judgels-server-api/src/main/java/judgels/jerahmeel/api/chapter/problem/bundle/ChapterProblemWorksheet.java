package judgels.jerahmeel.api.chapter.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.bundle.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemWorksheet.class)
public interface ChapterProblemWorksheet extends judgels.jerahmeel.api.chapter.problem.ChapterProblemWorksheet {
    ProblemWorksheet getWorksheet();
    ProblemProgress getProgress();
    Optional<ProblemEditorialInfo> getEditorial();

    class Builder extends ImmutableChapterProblemWorksheet.Builder{}
}
