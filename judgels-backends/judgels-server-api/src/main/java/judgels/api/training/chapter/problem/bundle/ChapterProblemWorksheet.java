package judgels.api.training.chapter.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.api.catalog.problem.ProblemEditorialInfo;
import judgels.api.catalog.problem.bundle.ProblemWorksheet;
import judgels.api.training.stats.ProblemProgress;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemWorksheet.class)
public interface ChapterProblemWorksheet extends judgels.api.training.chapter.problem.ChapterProblemWorksheet {
    ProblemWorksheet getWorksheet();
    ProblemProgress getProgress();
    Optional<ProblemEditorialInfo> getEditorial();

    class Builder extends ImmutableChapterProblemWorksheet.Builder{}
}
