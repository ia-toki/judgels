package judgels.fs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableFileInfo.class)
public interface FileInfo {
    String getName();
    long getSize();
    Instant getLastModifiedTime();

    class Builder extends ImmutableFileInfo.Builder {}
}
