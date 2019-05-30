package judgels.gabriel.moe;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.nio.file.Path;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableMoeConfiguration.class)
public interface MoeConfiguration {
    Path getIsolatePath();
    Path getIwrapperPath();

    class Builder extends ImmutableMoeConfiguration.Builder {}
}
