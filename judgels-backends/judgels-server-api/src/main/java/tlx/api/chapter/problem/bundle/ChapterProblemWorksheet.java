package tlx.api.chapter.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.api.problem.ProblemEditorialInfo;
import judgels.api.problem.ProblemProgress;
import judgels.api.problem.bundle.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemWorksheet.class)
public interface ChapterProblemWorksheet extends tlx.api.chapter.problem.ChapterProblemWorksheet {
    ProblemWorksheet getWorksheet();
    ProblemProgress getProgress();
    Optional<ProblemEditorialInfo> getEditorial();

    class Builder extends ImmutableChapterProblemWorksheet.Builder{}
}
