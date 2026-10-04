package judgels.api.training.chapter.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.api.catalog.problem.ProblemEditorialInfo;
import judgels.api.catalog.problem.programming.ProblemSkeleton;
import judgels.api.catalog.problem.programming.ProblemWorksheet;
import judgels.api.submission.programming.Submission;
import judgels.api.training.stats.ProblemProgress;
import judgels.grading.api.SubmissionSource;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemWorksheet.class)
public interface ChapterProblemWorksheet extends judgels.api.training.chapter.problem.ChapterProblemWorksheet  {
    ProblemWorksheet getWorksheet();
    Set<ProblemSkeleton> getSkeletons();
    Optional<Submission> getLastSubmission();
    Optional<SubmissionSource> getLastSubmissionSource();
    List<List<String>> getProblemSetProblemPaths();
    ProblemProgress getProgress();
    Optional<ProblemEditorialInfo> getEditorial();

    class Builder extends ImmutableChapterProblemWorksheet.Builder{}
}
