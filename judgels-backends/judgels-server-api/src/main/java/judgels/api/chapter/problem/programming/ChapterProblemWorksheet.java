package judgels.api.chapter.problem.programming;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.api.problem.ProblemEditorialInfo;
import judgels.api.problem.ProblemProgress;
import judgels.api.problem.programming.ProblemSkeleton;
import judgels.api.problem.programming.ProblemWorksheet;
import judgels.api.submission.programming.Submission;
import judgels.gabriel.api.SubmissionSource;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemWorksheet.class)
public interface ChapterProblemWorksheet extends judgels.api.chapter.problem.ChapterProblemWorksheet  {
    ProblemWorksheet getWorksheet();
    Set<ProblemSkeleton> getSkeletons();
    Optional<Submission> getLastSubmission();
    Optional<SubmissionSource> getLastSubmissionSource();
    List<List<String>> getProblemSetProblemPaths();
    ProblemProgress getProgress();
    Optional<ProblemEditorialInfo> getEditorial();

    class Builder extends ImmutableChapterProblemWorksheet.Builder{}
}
