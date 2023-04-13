package judgels.jerahmeel.api.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterProgress.class)
public interface ChapterProgress {
    int getSolvedProblems();
    int getTotalProblems();

    class Builder extends ImmutableChapterProgress.Builder {}
}
