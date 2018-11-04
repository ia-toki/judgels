package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIoiStyleModuleConfig.class)
public interface IoiStyleModuleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }

    @Value.Default
    default boolean getUsingLastAffectingPenalty() {
        return false;
    }

    class Builder extends ImmutableIoiStyleModuleConfig.Builder {}
}
