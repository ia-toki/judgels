package judgels.jerahmeel.api.archive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableArchiveUpdateData.class)
public interface ArchiveUpdateData {
    Optional<String> getSlug();
    Optional<String> getName();
    Optional<String> getCategory();
    Optional<String> getDescription();

    class Builder extends ImmutableArchiveUpdateData.Builder {}
}
