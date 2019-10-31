package judgels.jerahmeel.api.chapter.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.problem.ProblemInfo;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemsResponse.class)
public interface ChapterProblemsResponse {
    List<ChapterProblem> getData();
    Map<String, ProblemInfo> getProblemsMap();

    class Builder extends ImmutableChapterProblemsResponse.Builder {}
}
