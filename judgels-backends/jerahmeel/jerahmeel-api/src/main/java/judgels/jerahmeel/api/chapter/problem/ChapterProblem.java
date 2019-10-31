package judgels.jerahmeel.api.chapter.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.sandalphon.api.problem.ProblemType;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblem.class)
public interface ChapterProblem {
    String getAlias();
    String getProblemJid();
    ProblemType getType();

    class Builder extends ImmutableChapterProblem.Builder {}
}
