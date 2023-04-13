package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonTypeName("BUNDLE")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableBundleStyleModuleConfig.class)
public interface BundleStyleModuleConfig extends StyleModuleConfig {

    class Builder extends ImmutableBundleStyleModuleConfig.Builder {}
}
