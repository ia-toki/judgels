package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@JsonTypeName("EDITORIAL")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableEditorialModuleConfig.class)
public interface EditorialModuleConfig extends ModuleConfig {
    EditorialModuleConfig DEFAULT = new Builder()
            .build();

    Optional<String> getPreface();

    class Builder extends ImmutableEditorialModuleConfig.Builder {}
}
