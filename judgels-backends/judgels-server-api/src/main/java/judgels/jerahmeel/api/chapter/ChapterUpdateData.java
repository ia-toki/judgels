package judgels.jerahmeel.api.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterUpdateData.class)
public interface ChapterUpdateData {
    Optional<String> getName();

    class Builder extends ImmutableChapterUpdateData.Builder {}
}
