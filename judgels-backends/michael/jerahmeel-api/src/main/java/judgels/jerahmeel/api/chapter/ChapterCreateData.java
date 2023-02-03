package judgels.jerahmeel.api.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterCreateData.class)
public interface ChapterCreateData {
    String getName();

    class Builder extends ImmutableChapterCreateData.Builder {}
}
