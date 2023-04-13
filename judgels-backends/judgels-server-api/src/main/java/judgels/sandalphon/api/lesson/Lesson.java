package judgels.sandalphon.api.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLesson.class)
public interface Lesson {
    long getId();
    String getJid();
    String getSlug();
    String getAuthorJid();
    String getAdditionalNote();
    Instant getLastUpdateTime();

    class Builder extends ImmutableLesson.Builder {}
}
