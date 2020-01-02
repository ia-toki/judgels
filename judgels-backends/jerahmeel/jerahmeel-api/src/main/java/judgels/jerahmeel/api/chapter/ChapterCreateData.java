package judgels.jerahmeel.api.chapter;

import org.immutables.value.Value;

@Value.Immutable
public interface ChapterCreateData {
    String getName();

    class Builder extends ImmutableChapterCreateData.Builder {}
}
