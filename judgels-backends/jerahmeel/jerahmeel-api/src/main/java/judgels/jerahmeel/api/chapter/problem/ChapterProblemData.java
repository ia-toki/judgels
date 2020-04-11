package judgels.jerahmeel.api.chapter.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.problem.ProblemType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblemData.class)
public interface ChapterProblemData {
    String getAlias();
    String getSlug();
    ProblemType getType();

    class Builder extends ImmutableChapterProblemData.Builder {}
}
