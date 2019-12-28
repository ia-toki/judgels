package judgels.jerahmeel.api.archive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableArchive.class)
public interface Archive {
    long getId();
    String getJid();
    String getName();
    String getDescription();

    class Builder extends ImmutableArchive.Builder {}
}
