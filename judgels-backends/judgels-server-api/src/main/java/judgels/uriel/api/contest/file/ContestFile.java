package judgels.uriel.api.contest.file;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestFile.class)
public interface ContestFile {
    String getName();
    long getSize();
    Instant getLastModifiedTime();

    class Builder extends ImmutableContestFile.Builder {}
}
