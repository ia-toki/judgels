package judgels.jerahmeel.api.chapter.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemsResponse.class)
public interface ChapterProblemsResponse {
    List<ChapterProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();
    Map<String, ProblemProgress> getProblemProgressesMap();

    class Builder extends ImmutableChapterProblemsResponse.Builder {}
}
