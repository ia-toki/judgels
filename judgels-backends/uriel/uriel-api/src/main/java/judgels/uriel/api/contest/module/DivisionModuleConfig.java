package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@JsonTypeName("DIVISION")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableDivisionModuleConfig.class)
public interface DivisionModuleConfig extends ModuleConfig {
    DivisionModuleConfig DEFAULT = new Builder()
            .division(1)
            .build();

    int getDivision();

    class Builder extends ImmutableDivisionModuleConfig.Builder {}
}
