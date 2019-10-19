package judgels.jerahmeel.api.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterInfo.class)
public interface ChapterInfo {
    String getName();

    class Builder extends ImmutableChapterInfo.Builder {}
}
