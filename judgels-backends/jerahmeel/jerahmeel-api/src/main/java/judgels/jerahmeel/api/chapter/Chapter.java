package judgels.jerahmeel.api.chapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapter.class)
public interface Chapter {
    long getId();
    String getJid();
    String getName();
    String getDescription();

    class Builder extends ImmutableChapter.Builder {}
}
