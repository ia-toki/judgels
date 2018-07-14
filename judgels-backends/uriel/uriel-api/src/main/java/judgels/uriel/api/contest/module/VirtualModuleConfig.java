package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableVirtualModuleConfig.class)
public interface VirtualModuleConfig {
    Duration getVirtualDuration();

    class Builder extends ImmutableVirtualModuleConfig.Builder {}
}
