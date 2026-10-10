package judgels.api.catalog.problem;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemFile.class)
public interface ProblemFile {
    String getName();
    long getSize();
    Instant getLastModifiedTime();

    class Builder extends ImmutableProblemFile.Builder {}
}
