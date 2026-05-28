package tlx.api.chapter.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemProgress;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemsResponse.class)
public interface ChapterProblemsResponse {
    List<ChapterProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, List<List<String>>> getProblemSetProblemPathsMap();
    Map<String, ProblemProgress> getProblemProgressesMap();

    class Builder extends ImmutableChapterProblemsResponse.Builder {}
}
