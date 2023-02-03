package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@JsonTypeName("IOI")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableIoiStyleModuleConfig.class)
public interface IoiStyleModuleConfig extends StyleModuleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    @Override
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }

    @Value.Default
    default boolean getUsingLastAffectingPenalty() {
        return false;
    }

    @Value.Default
    default boolean getUsingMaxScorePerSubtask() {
        return false;
    }

    class Builder extends ImmutableIoiStyleModuleConfig.Builder {}
}
