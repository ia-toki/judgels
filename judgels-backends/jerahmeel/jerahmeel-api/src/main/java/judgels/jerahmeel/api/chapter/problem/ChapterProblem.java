package judgels.jerahmeel.api.chapter.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProblem.class)
public interface ChapterProblem {
    String getAlias();
    String getProblemJid();

    class Builder extends ImmutableChapterProblem.Builder {}
}
