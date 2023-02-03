package judgels.jerahmeel.api.archive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableArchiveCreateData.class)
public interface ArchiveCreateData {
    String getSlug();
    String getName();
    String getCategory();
    Optional<String> getDescription();

    class Builder extends ImmutableArchiveCreateData.Builder {}
}
