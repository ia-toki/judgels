package judgels.jerahmeel.api.chapter.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import java.util.Set;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.programming.ProblemSkeleton;
import judgels.sandalphon.api.problem.programming.ProblemWorksheet;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemWorksheet.class)
public interface ChapterProblemWorksheet extends judgels.jerahmeel.api.chapter.problem.ChapterProblemWorksheet  {
    ProblemWorksheet getWorksheet();
    Set<ProblemSkeleton> getSkeletons();
    ProblemProgress getProgress();
    Optional<ProblemEditorialInfo> getEditorial();

    class Builder extends ImmutableChapterProblemWorksheet.Builder{}
}
