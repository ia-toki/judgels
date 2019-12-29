package judgels.jerahmeel.api.archive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableArchive.class)
public interface Archive {
    long getId();
    String getJid();
    String getSlug();
    String getName();
    String getDescription();
    String getCategory();

    class Builder extends ImmutableArchive.Builder {}
}
