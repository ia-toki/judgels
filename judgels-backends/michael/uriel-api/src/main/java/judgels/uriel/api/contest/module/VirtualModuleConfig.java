package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Duration;
import org.immutables.value.Value;

@JsonTypeName("VIRTUAL")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableVirtualModuleConfig.class)
public interface VirtualModuleConfig extends ModuleConfig {
    VirtualModuleConfig DEFAULT = new Builder()
            .virtualDuration(Duration.ofHours(5))
            .build();

    Duration getVirtualDuration();

    class Builder extends ImmutableVirtualModuleConfig.Builder {}
}
