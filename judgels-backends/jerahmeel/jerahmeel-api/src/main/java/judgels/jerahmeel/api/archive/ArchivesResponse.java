package judgels.jerahmeel.api.archive;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableArchivesResponse.class)
public interface ArchivesResponse {
    List<Archive> getData();

    class Builder extends ImmutableArchivesResponse.Builder {}
}
