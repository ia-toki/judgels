package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableStyleModuleConfig.class)
public interface StyleModuleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }


    class Builder extends ImmutableStyleModuleConfig.Builder {}
}
