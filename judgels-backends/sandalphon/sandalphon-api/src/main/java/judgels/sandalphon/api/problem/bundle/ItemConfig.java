package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItemConfig.class)
public interface ItemConfig {
    String getStatement();

    class Builder extends ImmutableItemConfig.Builder {}
}
