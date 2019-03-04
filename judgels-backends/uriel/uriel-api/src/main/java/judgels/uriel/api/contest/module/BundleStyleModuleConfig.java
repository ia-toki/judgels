package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBundleStyleModuleConfig.class)
public interface BundleStyleModuleConfig extends StyleModuleConfig {

    class Builder extends ImmutableBundleStyleModuleConfig.Builder {}
}
