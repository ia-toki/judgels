package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@JsonTypeName("TROC")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableTrocStyleModuleConfig.class)
public interface TrocStyleModuleConfig extends StyleModuleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    @Override
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }

    @Value.Default
    default long getWrongSubmissionPenalty() {
        return 4;
    }

    class Builder extends ImmutableTrocStyleModuleConfig.Builder {}
}
